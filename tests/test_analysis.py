from kzp_lab06.analysis import filter_by_sold, get_unique_films, revenue_by_hall


def test_filter_by_sold() -> None:
    records = [
        {"film": "A", "sold": 100},
        {"film": "B", "sold": 5},
    ]
    assert len(filter_by_sold(records, 10)) == 1


def test_revenue_by_hall() -> None:
    records = [
        {"hall": "Hall 1", "price": 100.0, "sold": 2},
        {"hall": "Hall 1", "price": 50.0, "sold": 1},
    ]
    assert revenue_by_hall(records) == {"Hall 1": 250.0}


def test_unique_films() -> None:
    records = [
        {"film": "Avatar"},
        {"film": "Avatar"},
        {"film": "Dune"},
    ]
    assert get_unique_films(records) == {"Avatar", "Dune"}