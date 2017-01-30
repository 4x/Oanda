SELECT c."time" ,	
	openMid,
	highMid,
	lowMid,
	closeMid,
	volume ,
	complete,
	f.broken,
	f.brokenTime,
	f.direction
FROM %FRACTALS%_%INSTRUMENT%_%STEP% f 
JOIN %CANDLES%_%INSTRUMENT%_%STEP% c ON f."time"=c."time"
WHERE f.direction = :dir AND f.broken = TRUE
ORDER BY f."time" DESC
LIMIT 1
