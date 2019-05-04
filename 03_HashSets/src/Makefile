files_compile=-cp .:*  hashsets_benchmark/*.java
files_run=-cp .:* hashsets_benchmark/Benchmark

compile:
	javac $(files_compile)
	mkdir -p ../plots/benchmark-1; ln -fs ../plot.gp ../plots/benchmark-1/;
	mkdir -p ../plots/benchmark-2; ln -fs ../plot.gp ../plots/benchmark-2/;
	mkdir -p ../plots/benchmark-3; ln -fs ../plot.gp ../plots/benchmark-3/;

unit_tests:
	java $(files_run) unit_tests

benchmark-1:
#	java $(files_run) > ../plots/benchmark-1/output.dat
	java $(files_run)

benchmark-2:
	java $(files_run) 0 10000 1000 PerThread 0.05 0.05 0.9 5000 true 10 1 2 4 6 8 12 16 20 24 > ../plots/benchmark-2/output.dat

benchmark-3:
	java $(files_run) 0 1000000 1000 PerThread 1.0 0.0 0.0 0 true 10 1 2 4 6 8 12 16 20 24 > ../plots/benchmark-3/output.dat

plots:
	cd ../plots/benchmark-1; gnuplot plot.gp;
	cd ../plots/benchmark-2; gnuplot plot.gp;
	cd ../plots/benchmark-3; gnuplot plot.gp;

clear:
	rm -rf ../plots/benchmark-1
	rm -rf ../plots/benchmark-2
	rm -rf ../plots/benchmark-3
	rm -rf hashsets_benchmark/*.class