SELECT 
    trade.stockid,
    trade.timeid,
    -- trade.year,
    -- trade.season,
    trade.YR,
    trade.PE,
    trade.ClosePrice / s.BookValue AS PBR,
    -- s.year,
    -- s.season,
    s.eps as EPS,
    s.ROE as ROE,
    s.DebtRatio as DR    
FROM
    (SELECT 
        d.stockid AS stockid,
            t.timeid AS timeid,
            t.year AS year,
            t.month as month,
            t.season AS season,
            t.Date AS date,
            d.closeprice AS ClosePrice,
            d.YieldRate AS YR,
            d.PE AS PE
    FROM
        `trading` AS d
    JOIN `time` AS t ON d.TimeId = t.TimeId) AS trade
        JOIN
    season AS s 
    ON trade.stockid = s.stockid
        -- AND trade.year= s.year
        -- AND trade.season= s.season
        and s.year=(case when trade.season=1 then trade.year -1 else trade.year end)
        and s.season = (case when trade.season=1 then 4 else trade.season -1 end)        
WHERE
    trade.Date in ('20050331','20050516','20050815','20051114') and trade.stockid='1102';