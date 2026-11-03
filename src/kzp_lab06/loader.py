import csv
from pathlib import Path

from .errors import DataError
from .models import PremiumScreening, RegularScreening, Screening

REQUIRED_FIELDS = {"film", "hall", "start", "price", "sold", "type"}


def load_records(path: Path) -> list[Screening]:
    """Читає CSV-файл кінотеатру та створює об'єкти відповідних підтипів Screening."""
    try:
        with path.open("r", encoding="utf-8", newline="") as source:
            reader = csv.DictReader(source)
            if reader.fieldnames is None or set(reader.fieldnames) != REQUIRED_FIELDS:
                raise DataError("Некоректний заголовок CSV-файлу")

            records: list[Screening] = []
            for row_number, row in enumerate(reader, start=2):
                try:
                    film = row["film"].strip()
                    hall = row["hall"].strip()
                    start = row["start"].strip()
                    price = float(row["price"])
                    sold = int(row["sold"])
                    stype = row["type"].strip().lower()

                    if not film or not hall or not start or not stype:
                        raise DataError(f"Рядок {row_number}: текстове поле порожнє")

                    # Створення екземпляра залежно від типу сеансу
                    if stype == "regular":
                        screening: Screening = RegularScreening(
                            film=film,
                            hall=hall,
                            start=start,
                            price=price,
                            sold=sold,
                        )
                    elif stype == "premium":
                        screening = PremiumScreening(
                            film=film,
                            hall=hall,
                            start=start,
                            price=price,
                            sold=sold,
                        )
                    else:
                        raise DataError(
                            f"Рядок {row_number}: невідомий тип сеансу '{stype}'"
                        )

                    records.append(screening)

                except (KeyError, TypeError, ValueError) as error:
                    raise DataError(
                        f"Рядок {row_number}: некоректні дані ({error})"
                    ) from error

            return records
    except OSError as error:
        raise DataError(f"Не вдалося прочитати файл: {path}") from error