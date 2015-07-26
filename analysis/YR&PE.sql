SELECT 
    d.stockid AS stockid,
    t.year AS year,
    t.month AS month,
    t.day AS day,
    d.YieldRate AS YR,
    d.PE AS PE
FROM
    `trading` AS d
        JOIN
    `time` AS t ON d.TimeId = t.TimeId
WHERE
    (t.month = 1 AND t.day = 10)
        OR (t.month = 2 AND t.day = 10)
        OR (t.month = 3 AND t.day = 10)
        OR (t.month = 3 AND t.day = 10)
        OR (t.month = 3 AND t.day = 31)
        OR (t.month = 4 AND t.day = 10)
        OR (t.month = 5 AND t.day = 10)
        OR (t.month = 5 AND t.day = 15)
        OR (t.month = 6 AND t.day = 10)
        OR (t.month = 7 AND t.day = 10)
        OR (t.month = 8 AND t.day = 10)
        OR (t.month = 8 AND t.day = 14)
        OR (t.month = 9 AND t.day = 10)
        OR (t.month = 10 AND t.day = 10)
        OR (t.month = 11 AND t.day = 10)
        OR (t.month = 11 AND t.day = 14)
        OR (t.month = 12 AND t.day = 10)
ORDER BY t.year , t.month , t.day , d.stockid;

SELECT DISTINCT
    a.year, a.month, a.day
FROM
    (SELECT 
        d.stockid AS stockid,
            t.year AS year,
            t.month AS month,
            t.day AS day,
            d.YieldRate AS YR,
            d.PE AS PE
    FROM
        `trading` AS d
    JOIN `time` AS t ON d.TimeId = t.TimeId
    WHERE
        (t.month = 1 AND t.day = 10)
            OR (t.month = 2 AND t.day = 10)
            OR (t.month = 3 AND t.day = 10)
            OR (t.month = 3 AND t.day = 10)
            OR (t.month = 3 AND t.day = 31)
            OR (t.month = 4 AND t.day = 10)
            OR (t.month = 5 AND t.day = 10)
            OR (t.month = 5 AND t.day = 15)
            OR (t.month = 6 AND t.day = 10)
            OR (t.month = 7 AND t.day = 10)
            OR (t.month = 8 AND t.day = 10)
            OR (t.month = 8 AND t.day = 14)
            OR (t.month = 9 AND t.day = 10)
            OR (t.month = 10 AND t.day = 10)
            OR (t.month = 11 AND t.day = 10)
            OR (t.month = 11 AND t.day = 14)
            OR (t.month = 12 AND t.day = 10)
    ORDER BY t.year , t.month , t.day , d.stockid) AS a;