from pathlib import Path

from .models import ReportSummary


def format_report(summary: ReportSummary) -> str:
    """Форматує підсумок у текстовий звіт з окремими рядками."""
    unique_films = sorted(list(summary.unique_films), reverse=True)
    
    lines = [
        f"Всього сеансів: {summary.total_records}",
        f"Унікальні фільми ({len(unique_films)}): {', '.join(unique_films)}",
        f"Загальний виторг: {summary.total_revenue:.2f} грн",
        (
            "Найпопулярніший сеанс: "
            f"{summary.most_popular_film} ({summary.most_popular_sold} квитків)"
        ),
        "--- Виторг за залами ---",
    ]

    for hall, rev in sorted(summary.revenue_by_hall.items()):
        lines.append(f"{hall}: {rev:.2f} грн")

    return "\n".join(lines) + "\n"


def write_report(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as target:
        target.write(text)