from collections.abc import Iterable


def filter_by_sold(
    records: Iterable[dict[str, object]], threshold: int
) -> list[dict[str, object]]:
    """Відбір сеансів, де продано квитків більше за threshold (Comprehension)."""
    return [r for r in records if int(r["sold"]) > threshold]


def get_unique_films(records: Iterable[dict[str, object]]) -> set[str]:
    """Повертає множину унікальних назв фільмів."""
    return {str(r["film"]) for r in records}


def revenue_by_hall(records: list[dict[str, object]]) -> dict[str, float]:
    """Обчислює виторг за кожним залом у стабільному порядку."""
    halls = {str(r["hall"]) for r in records}
    return {
        hall: sum(
            float(r["price"]) * int(r["sold"])
            for r in records
            if r["hall"] == hall
        )
        for hall in sorted(halls)
    }


def make_summary(
    records: list[dict[str, object]],
    *metrics: str,
    minimum_sold: int = 10,
    **labels: str,
) -> dict[str, object]:
    """Формує підсумок та аналітику для відібраних сеансів."""
    selected = filter_by_sold(records, minimum_sold)

    total_revenue = sum(float(r["price"]) * int(r["sold"]) for r in records)
    most_popular = (
        max(records, key=lambda r: int(r["sold"])) if records else None
    )

    result: dict[str, object] = {
        "count": len(selected),
        "total_records": len(records),
        "unique_films": get_unique_films(records),
        "revenue_by_hall": revenue_by_hall(records),
        "total_revenue": total_revenue,
        "most_popular_film": (
            str(most_popular["film"]) if most_popular else "N/A"
        ),
        "most_popular_sold": int(most_popular["sold"]) if most_popular else 0,
    }

    for metric in metrics:
        result[labels.get(metric, metric)] = sum(
            float(r[metric]) for r in selected
        )

    return result