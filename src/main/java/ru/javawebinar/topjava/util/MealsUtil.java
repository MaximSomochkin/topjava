package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class MealsUtil {
    public static void main(String[] args) {
        List<Meal> meals = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByStreams(meals, LocalTime.of(7, 0),
                LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        List<UserMealWithExcess> mealWithExcesses = new ArrayList<>();
        List<Meal> mealFilterDates = new ArrayList<>();


        for (Meal meal: meals)
            if(startTime.isBefore(meal.getDateTime().toLocalTime()) && endTime.isAfter(meal.getDateTime().toLocalTime()))
                mealFilterDates.add(meal);

        Map<LocalDate, Integer> dayMealMap = new HashMap<>();

        mealFilterDates.forEach(dayMeal ->
                dayMealMap.merge(dayMeal.getDateTime().toLocalDate(),dayMeal.getCalories(),
                        Integer::sum));

        dayMealMap.values().removeIf(calories -> calories<caloriesPerDay);

        mealFilterDates.forEach(meal -> {
            if (dayMealMap.containsKey(meal.getDateTime().toLocalDate()))
                mealWithExcesses.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),true));
        });
        return mealWithExcesses;

    }

    public static List<UserMealWithExcess> filteredByStreams(List<Meal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
               Map<LocalDate, Integer> calloriesSummByDay = meals.stream()
                .collect(Collectors.groupingBy(um->um.getDateTime().toLocalDate(),
                        Collectors.summingInt(Meal::getCalories)));
        return meals.stream()
                .filter(um->TimeUtil.isBetweenInclusive(um.getDateTime().toLocalTime(),startTime,endTime))
                .map(um->new UserMealWithExcess(um.getDateTime(),um.getDescription(),um.getCalories(),
                        calloriesSummByDay.get(um.getDateTime().toLocalDate())>caloriesPerDay))
                .collect(Collectors.toList());

    }
}
