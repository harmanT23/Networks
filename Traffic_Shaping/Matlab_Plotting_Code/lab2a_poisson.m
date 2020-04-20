clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[time_p, packetsize_p] = textread('output_poisson_receiver23.txt', '%f %f');
[packet_no_p, time_p2, packetsize_p2] = textread('poisson3.data', '%f %f %f');

times = zeros(1, length(time_p));
bytes = zeros(1, length(time_p));

timesp = zeros(1, length(time_p));
bytesp = zeros(1, length(time_p));

time_sum = 0;
bytes_sum = 0;

time_sump = 0;
bytes_sump = 0;

for i = 1 : length(time_p)
    time_sum = time_sum + time_p(i);
    bytes_sum = bytes_sum + packetsize_p(i);
    times(i) = time_sum;
    bytes(i) = bytes_sum;

    time_sump = time_sump + time_p2(i);
    bytes_sump = bytes_sump + packetsize_p2(i);
    timesp(i) = time_sump;
    bytesp(i) = bytes_sump;
end

figure(1);

subplot(2,2,1);
plot(times ./ 1000000, packetsize_p)
title("Traffic Sink Poisson");
xlabel("time (s)");
ylabel("bytes");

subplot(2,2,2);
plot(time_p2(1:10000) ./ 1000000, packetsize_p2(1:10000))
title("Poisson 3 Trace");
xlabel("time (s)");
ylabel("bytes");

subplot(2,2,3);
plot(times ./ 1000000, bytes)
title("Traffic Sink Arrival");
xlabel("time (s)");
ylabel("bytes");

subplot(2,2,4);
plot(time_p2(1:10000) ./ 1000000, bytesp)
title("Poisson 3 Trace Arrival");
xlabel("time (s)");
ylabel("bytes");

total_poisson_10K = bytes_sump
total_sink_10K = bytes_sum
