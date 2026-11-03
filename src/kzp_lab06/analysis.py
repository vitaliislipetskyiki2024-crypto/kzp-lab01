from collections.abc import Iterable

from .models import HasRevenue, ReportSummary, Screening


def filter_by_sold(
    records: Iterable[Screening], threshold: int
) -> list[Screening]:
    return [r for r in records if r.sold > threshold]


def get_unique_films(records: Iterable[Screening]) -> set[str]:
    return {r.film for r in records}


def revenue_by_hall(records: list[HasRevenue]) -> dict[str, float]:
    """Обчислює виторг за залами через поліморфний метод .revenue()."""
    screenings = [r for r in records if isinstance(r, Screening)]
    halls = {r.hall for r in screenings}
    return {
        hall: sum(
            r.revenue()
            for r in screenings
            if r.hall == hall
        )
        for hall in sorted(halls)
    }


def make_summary(
    records: list[Screening], minimum_sold: int = 10
) -> ReportSummary:
    selected = filter_by_sold(records, minimum_sold)
    total_revenue = sum(r.revenue() for r in records)
    most_popular = max(records, key=lambda r: r.sold) if records else None

    return ReportSummary(
        count=len(selected),
        total_records=len(records),
        unique_films=get_unique_films(records),
        revenue_by_hall=revenue_by_hall(records),
        total_revenue=total_revenue,
        most_popular_film=most_popular.film if most_popular else "N/A",
        most_popular_sold=most_popular.sold if most_popular else 0,
    )