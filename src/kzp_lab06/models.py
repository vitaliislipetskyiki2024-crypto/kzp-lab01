from dataclasses import dataclass
from typing import Protocol


class HasRevenue(Protocol):
    """Описує протокол об'єктів, які вміють рахувати виторг."""
    def revenue(self) -> float:
        ...


class Screening:
    """Базовий сеанс із перевіреним станом."""
    def __init__(self, film: str, hall: str, start: str, price: float, sold: int) -> None:
        self.film = film
        self.hall = hall
        self.start = start
        self.price = price
        self.sold = sold

    @property
    def price(self) -> float:
        return self._price

    @price.setter
    def price(self, value: float) -> None:
        if value < 0:
            raise ValueError("Ціна не може бути від'ємною")
        self._price = value

    @property
    def sold(self) -> int:
        return self._sold

    @sold.setter
    def sold(self, value: int) -> None:
        if value < 0:
            raise ValueError("Кількість квитків не може бути від'ємною")
        self._sold = value

    def revenue(self) -> float:
        raise NotImplementedError

    def __str__(self) -> str:
        return f"{self.film} ({self.hall}): {self.sold} квитків"

    def __repr__(self) -> str:
        return (
            f"{type(self).__name__}(film={self.film!r}, hall={self.hall!r}, "
            f"start={self.start!r}, price={self.price!r}, sold={self.sold!r})"
        )

    def __eq__(self, other: object) -> bool:
        if not isinstance(other, Screening):
            return NotImplemented
        return type(self) is type(other) and self.__dict__ == other.__dict__


class RegularScreening(Screening):
    """Звичайний сеанс."""
    def revenue(self) -> float:
        return self.sold * self.price


class PremiumScreening(Screening):
    """Преміум сеанс з націнкою 20%."""
    def revenue(self) -> float:
        return self.sold * self.price * 1.2


@dataclass(frozen=True)
class ReportSummary:
    """Зберігає незмінні підсумкові показники."""
    count: int
    total_records: int
    unique_films: set[str]
    revenue_by_hall: dict[str, float]
    total_revenue: float
    most_popular_film: str
    most_popular_sold: int