select count(*), [ sfile] 
from weka group by [ sfile]



select count(sword), [ sfile]
from FREEMIND group by  [ sfile] 
 order by count(sword) asc


select sword, [ stimes], [ sfile]
from weka  order by  [ stimes] asc


select sword, [ stimes], [ sfile]
from freemind  order by  [ stimes] desc


select sword, [ stimes], [ sfile]
from freemind  order by  [ stimes] asc
