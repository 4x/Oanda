WITH breaks AS (
	SELECT 
		row_number() OVER () as rnum,
		f."time", 
		f.direction,
		c.highAsk,
		c.lowBid
	FROM %FRACTALS%_%INSTRUMENT%_%STEP% f
		JOIN %CANDLES%_%INSTRUMENT%_%STEP% c ON c."time"=f."time"
	WHERE f."time" > 
		COALESCE((SELECT max("time") FROM %FRACTALS%_%INSTRUMENT%_%STEP% WHERE broken = true),  '1999-01-08')
	ORDER BY "time" DESC
),
brokenFractals AS (
	select 
		b1."time"
	from breaks b1
		join breaks b2 ON b1.direction = b2.direction 
			AND b2."time" < b1."time" 
			AND not exists 
				(select 1 from breaks b3 where b3.direction = b1.direction and b3.rnum < b1.rnum and b3.rnum > b2.rnum)
		join %CANDLES%_%INSTRUMENT%_%STEP% c on c."time" > b2."time" 
			and c."time" < b1."time" 
			and (b1.direction = 1 and c.highAsk > b2.highAsk 
				or b1.direction = -1 and c.lowBid < b2.lowBid)
)
UPDATE %FRACTALS%_%INSTRUMENT%_%STEP% 
SET broken = true, brokenTime = bf."time"
FROM brokenFractals bf 
WHERE %FRACTALS%_%INSTRUMENT%_%STEP%."time" = bf."time"



