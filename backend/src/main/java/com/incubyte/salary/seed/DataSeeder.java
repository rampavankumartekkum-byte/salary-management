package com.incubyte.salary.seed;

import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Seeds ~10,000 employees for local/demo use. Only runs under the "seed" profile so it never
 * fires against a real deployment by accident (see docker-compose.yml / README for how to
 * trigger it).
 *
 * Uses a fixed random seed so the generated dataset - and therefore any screenshots, the demo
 * video, and analytics numbers discussed in the interview - is reproducible across runs.
 */
@Component
@Profile("seed")
public class DataSeeder implements ApplicationRunner {

    private static final long RANDOM_SEED = 42L;
    private static final int TARGET_COUNT = 10_000;
    private static final int BATCH_SIZE = 500;

    private record CountryInfo(String country, String currency, Locale locale, double costOfLivingMultiplier) {
    }

    private static final List<CountryInfo> COUNTRIES = List.of(
            new CountryInfo("United States", "USD", Locale.forLanguageTag("en-US"), 1.0),
            new CountryInfo("India", "INR", new Locale("en", "IN"), 0.18),
            new CountryInfo("United Kingdom", "GBP", Locale.UK, 0.95),
            new CountryInfo("Germany", "EUR", Locale.GERMANY, 0.9),
            new CountryInfo("Canada", "CAD", Locale.CANADA, 0.85)
    );

    private record RoleInfo(String designation, String department, double salaryMultiplier) {
    }

    private static final List<RoleInfo> ROLES = List.of(
            new RoleInfo("Software Engineer I", "Engineering", 1.0),
            new RoleInfo("Software Engineer II", "Engineering", 1.35),
            new RoleInfo("Senior Software Engineer", "Engineering", 1.8),
            new RoleInfo("Engineering Manager", "Engineering", 2.4),
            new RoleInfo("QA Engineer", "Engineering", 0.9),
            new RoleInfo("Product Manager", "Product", 1.9),
            new RoleInfo("Product Designer", "Design", 1.4),
            new RoleInfo("HR Executive", "Human Resources", 0.85),
            new RoleInfo("HR Manager", "Human Resources", 1.7),
            new RoleInfo("Recruiter", "Human Resources", 1.0),
            new RoleInfo("Sales Executive", "Sales", 0.9),
            new RoleInfo("Sales Manager", "Sales", 1.75),
            new RoleInfo("Marketing Specialist", "Marketing", 1.0),
            new RoleInfo("Marketing Manager", "Marketing", 1.7),
            new RoleInfo("Financial Analyst", "Finance", 1.1),
            new RoleInfo("Finance Manager", "Finance", 1.9),
            new RoleInfo("Customer Support Associate", "Support", 0.7),
            new RoleInfo("Support Team Lead", "Support", 1.2)
    );

    private static final BigDecimal BASE_ANNUAL_SALARY_USD = BigDecimal.valueOf(60_000);

    private final JdbcTemplate jdbcTemplate;

    public DataSeeder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        Integer existing = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM employees", Integer.class);
        if (existing != null && existing > 0) {
            System.out.println("[seed] employees table already has " + existing + " rows - skipping seed.");
            return;
        }

        Random random = new Random(RANDOM_SEED);
        Faker faker = new Faker(random);

        String insertSql = """
                INSERT INTO employees
                    (employee_code, first_name, last_name, email, department, designation,
                     country, currency, base_salary, annual_bonus, employment_type, manager_name, date_joined)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        List<Object[]> batch = new java.util.ArrayList<>(BATCH_SIZE);
        int emailCollisionGuard = 0;

        for (int i = 1; i <= TARGET_COUNT; i++) {
            CountryInfo country = COUNTRIES.get(random.nextInt(COUNTRIES.size()));
            RoleInfo role = ROLES.get(random.nextInt(ROLES.size()));

            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String employeeCode = "ACME%05d".formatted(i);
            String email = (firstName + "." + lastName + emailCollisionGuard++ + "@acme-corp.example")
                    .toLowerCase(Locale.ROOT);

            BigDecimal salary = BASE_ANNUAL_SALARY_USD
                    .multiply(BigDecimal.valueOf(role.salaryMultiplier()))
                    .multiply(BigDecimal.valueOf(country.costOfLivingMultiplier()))
                    .multiply(BigDecimal.valueOf(0.85 + random.nextDouble() * 0.3)) // +/-15% individual variance
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal bonus = salary.multiply(BigDecimal.valueOf(0.05 + random.nextDouble() * 0.15))
                    .setScale(2, RoundingMode.HALF_UP);

            String employmentType = random.nextDouble() < 0.9 ? "FULL_TIME"
                    : (random.nextBoolean() ? "CONTRACT" : "PART_TIME");

            LocalDate dateJoined = LocalDate.now().minusDays(random.nextInt((int) ChronoUnit.DAYS.between(
                    LocalDate.now().minusYears(8), LocalDate.now())));

            String managerName = random.nextDouble() < 0.95
                    ? faker.name().fullName()
                    : null;

            batch.add(new Object[]{
                    employeeCode, firstName, lastName, email, role.department(), role.designation(),
                    country.country(), country.currency(), salary, bonus, employmentType, managerName, dateJoined
            });

            if (batch.size() == BATCH_SIZE) {
                jdbcTemplate.batchUpdate(insertSql, batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(insertSql, batch);
        }

        System.out.println("[seed] inserted " + TARGET_COUNT + " employees.");
    }
}
