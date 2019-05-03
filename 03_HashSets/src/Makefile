compile:
	javac -cp .:commons-math3-3.6.1.jar hashsets_benchmark/*.java
	mkdir -p ../plots/benchmark-1; ln -fs ../plot.gp ../plots/benchmark-1/;
	mkdir -p ../plots/benchmark-2; ln -fs ../plot.gp ../plots/benchmark-2/;
	mkdir -p ../plots/benchmark-3; ln -fs ../plot.gp ../plots/benchmark-3/;

benchmark-1:
#	java -cp .:commons-math3-3.6.1.jar hashsets_benchmark/Benchmark > ../plots/benchmark-1/output.dat
	java -cp .:commons-math3-3.6.1.jar hashsets_benchmark/Benchmark

benchmark-2:
	java -cp .:commons-math3-3.6.1.jar hashsets_benchmark/Benchmark 0 10000 1000 PerThread 0.05 0.05 0.9 5000 true 10 1 2 4 6 8 12 16 20 24 > ../plots/benchmark-2/output.dat

benchmark-3:
	java -cp .:commons-math3-3.6.1.jar hashsets_benchmark/Benchmark 0 1000000 1000 PerThread 1.0 0.0 0.0 0 true 10 1 2 4 6 8 12 16 20 24 > ../plots/benchmark-3/output.dat

plots:
	cd ../plots/benchmark-1; gnuplot plot.gp;
	cd ../plots/benchmark-2; gnuplot plot.gp;
	cd ../plots/benchmark-3; gnuplot plot.gp;

clear:
	rm -rf ../plots/benchmark-1
	rm -rf ../plots/benchmark-2
	rm -rf ../plots/benchmark-3
	rm -rf hashsets_benchmark/*.class