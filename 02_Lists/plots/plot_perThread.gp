set term postscript color eps enhanced 28
set output "performance_lists_per_thread.eps"

set size 1.3,1.3

set tmargin 1
set lmargin 12
set rmargin 2
set bmargin 3.5

set xlabel "Threads"
set ylabel "Throughput per thread (operations/s)"
set grid y

set key top right

set xrange [1:24]
set xtics (1,2,4,6,8,12,16,20,24)

set mytics 2

set style line 1 lt 2 lc 1 lw 2 pt 1 ps 3
set style line 2 lt 2 lc 2 lw 2 pt 2 ps 3
set style line 3 lt 2 lc 3 lw 2 pt 3 ps 3
set style line 4 lt 2 lc 4 lw 2 pt 4 ps 3
set style line 5 lt 2 lc 5 lw 2 pt 5 ps 3

# prepare individual files
!grep CoarseGrainLockedList output.dat > CoarseGrainLockedList.dat
!grep FineGrainLockedList output.dat > FineGrainLockedList.dat
!grep OptimisticLockedList output.dat > OptimisticLockedList.dat
!grep LazyList output.dat > LazyList.dat
!grep LockFreeList output.dat > LockFreeList.dat

plot \
	'CoarseGrainLockedList.dat' using ($2):($6):($7) with yerrorbars ls 1 title "Coarse-grain", \
	'' using ($2):($6):($7) with lines ls 1 notitle , \
	'FineGrainLockedList.dat' using ($2):($6):($7) with yerrorbars ls 2 title "Fine-grain", \
	'' using ($2):($6):($7) with lines ls 2 notitle, \
	'OptimisticLockedList.dat' using ($2):($6):($7) with yerrorbars ls 3 title "Optimistic", \
	'' using ($2):($6):($7) with lines ls 3 notitle, \
	'LazyList.dat' using ($2):($6):($7) with yerrorbars ls 4 title "Lazy", \
	'' using (($2):($6):($7) with lines ls 4 notitle, \
	'LockFreeList.dat' using ($2):($6):($7) with yerrorbars ls 5 title "Lock-free", \
	'' using ($2):($6):($7) with lines ls 5 notitle

# remove individual files
!rm CoarseGrainLockedList.dat
!rm FineGrainLockedList.dat
!rm OptimisticLockedList.dat
!rm LazyList.dat
!rm LockFreeList.dat

!epstopdf performance_lists_per_thread.eps
!rm performance_lists_per_thread.eps
quit
