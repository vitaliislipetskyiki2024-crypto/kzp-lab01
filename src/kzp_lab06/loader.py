import csv
from pathlib import Path

from .errors import DataError

REQUIRED_FIELDS = {"film", "hall", "start", "price", "sold"}


def load_records(path: Path) -> list[dict[str, object]]:
    """Читає CSV-файл кінотеатру, перетворює числа та перевіряє типи."""
    try:
        with path.open("r", encoding="utf-8", newline="") as source:
            reader = csv.DictReader(source)
            if reader.fieldnames is None or set(reader.fieldnames) != REQUIRED_FIELDS:
                raise DataError("Некоректний заголовок CSV-файлу")

            records: list[dict[str, object]] = []
            for row_number, row in enumerate(reader, start=2):
                try:
                    film = row["film"].strip()
                    hall = row["hall"].strip()
                    start = row["start"].strip()
                    price = float(row["price"])
                    sold = int(row["sold"])
                except (AttributeError, KeyError, TypeError, ValueError) as error:
                    raise DataError(
                        f"Рядок {row_number}: некоректне числове поле"
                    ) from error

                if not film or not hall or not start:
                    raise DataError(f"Рядок {row_number}: текстове поле порожнє")
                if price < 0 or sold < 0:
                    raise DataError(
                        f"Рядок {row_number}: price та sold не можуть бути від'ємними"
                    )

                records.append(
                    {
                        "film": film,
                        "hall": hall,
                        "start": start,
                        "price": price,
                        "sold": sold,
                    }
                )
            return records
    except OSError as error:
        raise DataError(f"Не вдалося прочитати файл: {path}") from error