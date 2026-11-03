import pytest
from kzp_lab06.models import PremiumScreening, RegularScreening, ReportSummary


def test_property_validation() -> None:
    s = RegularScreening("Film", "H1", "12:00", 100.0, 10)
    with pytest.raises(ValueError):
        s.price = -50.0

    with pytest.raises(ValueError):
        s.sold = -1


def test_polymorphic_revenue() -> None:
    reg = RegularScreening("Film A", "H1", "12:00", 100.0, 10)
    prem = PremiumScreening("Film B", "H1", "14:00", 100.0, 10)

    assert reg.revenue() == 1000.0
    assert prem.revenue() == 1200.0  # 100 * 1.2 * 10


def test_summary_is_immutable() -> None:
    summary = ReportSummary(
        count=1,
        total_records=1,
        unique_films={"Film"},
        revenue_by_hall={"H1": 1000.0},
        total_revenue=1000.0,
        most_popular_film="Film",
        most_popular_sold=10,
    )
    with pytest.raises(AttributeError):
        summary.count = 5


def test_equality() -> None:
    s1 = RegularScreening("Film", "H1", "12:00", 100.0, 10)
    s2 = RegularScreening("Film", "H1", "12:00", 100.0, 10)
    assert s1 == s2