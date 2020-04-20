clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[time_p, packetsize_p] = textread('output_poisson_receiver31.txt', '%f %f');
[packet_no_p, time_p2, packetsize_p2] = textread('poisson3.data', '%f %f %f');
[time_p3, packetsize_p3, bufferSize, tokens] = textread('bucket31.txt', '%f %f %f %f');

times = zeros(1, length(time_p));
bytes = zeros(1, length(time_p));

timesp = zeros(1, length(time_p));
bytesp = zeros(1, length(time_p));

timesb = zeros(1, length(time_p));
bytesb = zeros(1, length(time_p));

time_sum = 0;
bytes_sum = 0;

time_sump = 0;
bytes_sump = 0;

time_sumb = 0;
bytes_sumb = 0;

for i = 1 : length(time_p)
    time_sum = time_sum + time_p(i);
    bytes_sum = bytes_sum + packetsize_p(i);
    times(i) = time_sum;
    bytes(i) = bytes_sum;

    bytes_sump = bytes_sump + packetsize_p2(i);
    bytesp(i) = bytes_sump;


    time_sumb = time_sumb + time_p3(i);
    bytes_sumb = bytes_sumb + packetsize_p3(i);
    timesb(i) = time_sumb;
    bytesb(i) = bytes_sumb;
end

figure(1);

subplot(3,1,1);
plot(times ./1000000, bytes)
title("Traffic Sink Arrival");
xlabel("time (s)");
ylabel("bytes");

subplot(3,1,2);
plot(timesb./1000000, bytesb)
title("Token Bucket Arrival");
xlabel("time (s)");
ylabel("bytes");

subplot(3,1,3);
plot(time_p2(1:10000)./1000000, bytesp)
title("Poisson 3 Trace Arrival");
xlabel("time (s)");
ylabel("bytes");

figure(2);
subplot(2,1,1);
plot(timesb./1000000, bufferSize(1:10000))
title("Buffer Size");
xlabel("time (s)");
ylabel("bytes");

subplot(2,1,2);
plot(timesb./1000000, tokens(1:10000))
title("Contents of Token Bucket (no. tokens)");
xlabel("time (s)");
ylabel("Tokens");

total_poisson_10K = bytes_sump
total_sink_10K = bytes_sum
