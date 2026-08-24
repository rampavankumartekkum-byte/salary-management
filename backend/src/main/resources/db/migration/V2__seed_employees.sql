INSERT INTO employees (
    employee_code,
    first_name,
    last_name,
    email,
    department,
    designation,
    country,
    currency,
    base_salary,
    annual_bonus,
    employment_type,
    manager_name,
    date_joined
)
SELECT
    'EMP' || LPAD(gs::text, 5, '0'),
    'First' || gs,
    'Last' || gs,
    'employee' || gs || '@acme.com',
    (ARRAY[
        'Engineering',
        'Finance',
        'HR',
        'Sales',
        'Marketing',
        'Operations'
    ])[1 + floor(random() * 6)::int],
    (ARRAY[
        'Software Engineer',
        'Senior Engineer',
        'Manager',
        'Analyst',
        'HR Specialist'
    ])[1 + floor(random() * 5)::int],
    (ARRAY[
        'USA',
        'India',
        'UK',
        'Germany',
        'Canada'
    ])[1 + floor(random() * 5)::int],
    (ARRAY[
        'USD',
        'INR',
        'GBP',
        'EUR',
        'CAD'
    ])[1 + floor(random() * 5)::int],
    round((40000 + random() * 110000)::numeric, 2),
    round((random() * 20000)::numeric, 2),
    'FULL_TIME',
    NULL,
    CURRENT_DATE - floor(random() * 3650)::int
FROM generate_series(1, 10000) AS gs;