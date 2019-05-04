set term postscript color eps enhanced 28
set output "performance_hashSets.eps"

set size 1.3,1.3

set tmargin 1
set lmargin 12
set rmargin 2
set bmargin 4.5

set xlabel "Threads"
set ylabel "Total throughput (operations/s)"
set grid y

set key top left

set xrange [1:24]
set xtics (1,2,4,6,8,12,16,20,24)

set mytics 2

# prepare individual files
!grep JavaConcurrentHashMapWrapper output.dat > JavaConcurrentHashMapWrapper.dat
!grep CoarseGrainLockedHashSet output.dat > CoarseGrainLockedHashSet.dat
!grep -w FineGrainLockedHashSet output.dat > FineGrainLockedHashSet.dat
!grep RefinableFineGrainLockedHashSet output.dat > RefinableFineGrainLockedHashSet.dat
!grep LockFreeHashSet output.dat > LockFreeHashSet.dat
!grep FineGrainLockedHashSetWithLocks output.dat > FineGrainLockedHashSetWithLocks.dat

### columns
# 1: 	[set class name]
# 2: 	[number of threads]
# 3: 	[measurement time in seconds]
# 4: 	[ops per second, avg]
# 5: 	[same, std. dev.]
# 6: 	[ops per second per thread, avg]
# 7: 	[same, std. dev]
# 8: 	[size of final set, avg]
# 9: 	[same, std. dev]
# 10:	[number of resizes, avg]
# 11:	[same, std. dev]

plot \
	'JavaConcurrentHashMapWrapper.dat' using ($2):($4):($5) with yerrorbars ls 1 title "Java Conc. HM", \
	'' using ($2):($4) with lines ls 1 notitle , \
 	'CoarseGrainLockedHashSet.dat' using ($2):($4):($5) with yerrorbars ls 2 title "Coarse lock", \
 	'' using ($2):($4) with lines ls 2 notitle, \
 	'FineGrainLockedHashSet.dat' using ($2):($4):($5) with yerrorbars ls 3 title "Fine lock", \
 	'' using ($2):($4) with lines ls 3 notitle , \
	'RefinableFineGrainLockedHashSet.dat' using ($2):($4):($5) with yerrorbars ls 4 title "Fine lock refinable", \
	'' using ($2):($4) with lines ls 4 notitle, \
	'LockFreeHashSet.dat' using ($2):($4):($5) with yerrorbars ls 5 title "Lock-free", \
	'' using ($2):($4) with lines ls 5 notitle, \
	'FineGrainLockedHashSetWithLocks.dat' using ($2):($4):($5) with yerrorbars ls 6 title "Fine lock Read/Write", \
     	'' using ($2):($4) with lines ls 6 notitle

# remove individual files
!rm JavaConcurrentHashMapWrapper.dat
!rm CoarseGrainLockedHashSet.dat
!rm FineGrainLockedHashSet.dat
!rm RefinableFineGrainLockedHashSet.dat
!rm LockFreeHashSet.dat
!rm FineGrainLockedHashSetWithLocks.dat

!epstopdf performance_hashSets.eps
!rm performance_hashSets.eps
quit
