files_compile=-cp .:*  hashsets_benchmark/*.java
files_run=-cp .:* hashsets_benchmark/Benchmark

compile:
	javac $(files_compile)
	mkdir -p ../plots/benchmark-1; ln -fs ../plot.gp ../plots/benchmark-1/;
	mkdir -p ../plots/benchmark-2; ln -fs ../plot.gp ../plots/benchmark-2/;
	mkdir -p ../plots/benchmark-3; ln -fs ../plot.gp ../plots/benchmark-3/;
	mkdir -p ../plots/benchmark-4; ln -fs ../plot.gp ../plots/benchmark-4/;

unit_tests:
	java $(files_run) unit_tests

benchmark-1:
	java $(files_run) > ../plots/benchmark-1/output.dat

benchmark-2:
	java $(files_run) 4 0 10000 9 1 90 2000 true 3000 3 > ../plots/benchmark-2/output.dat

benchmark-3:
	java $(files_run) 4 0 100000 100 0 0 0 true 3000 3 > ../plots/benchmark-3/output.dat

benchmark-4:
	java $(files_run) 32768 0 100000 100 0 0 0 true 3000 3 > ../plots/benchmark-4/output.dat

plots:
	cd ../plots/benchmark-1; gnuplot plot.gp;
	cd ../plots/benchmark-2; gnuplot plot.gp;
	cd ../plots/benchmark-3; gnuplot plot.gp;
	cd ../plots/benchmark-4; gnuplot plot.gp;

clear:
	rm -rf ../plots/benchmark-1
	rm -rf ../plots/benchmark-2
	rm -rf ../plots/benchmark-3
	rm -rf ../plots/benchmark-4
	rm -rf hashsets_benchmark/*.class