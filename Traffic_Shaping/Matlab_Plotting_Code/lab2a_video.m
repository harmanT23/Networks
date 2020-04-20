clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[time_p, packetsize_p] = textread('output_video_receiver.txt', '%f %f');
[time_p3, packetsize_p3, bufferSize, tokens] = textread('bucketVideo.txt', '%f %f %f %f');
[index, time, type_f, framesize_f, dummy1, dymmy2, dymmy3 ] = textread('movietrace.data.txt', '%f %f %c %f %f %f %f');

times = zeros(1, length(time_p));
bytes = zeros(1, length(time_p));

timesp = zeros(1, length(time_p));
bytesp = zeros(1, 1000);

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
    times(i) = time_sum;
    bytes(i) = bytes_sum;
    bytes_sum = bytes_sum + packetsize_p(i);

    time_sumb = time_sumb + time_p3(i);
    bytes_sumb = bytes_sumb + packetsize_p3(i);
    timesb(i) = time_sumb;
    bytesb(i) = bytes_sumb;
end

for i = 1: 1000
    
    bytes_sump = bytes_sump + framesize_f(i);
    bytesp(i) = bytes_sump;
    
end

figure(1);

subplot(3,1,1);
plot(times ./1000000, bytes)
title("Traffic Sink Arrival Video");
xlabel("time (s)");
ylabel("bytes");

subplot(3,1,2);
plot(timesb ./1000000, bytesb)
title("Token Bucket Arrival Video");
xlabel("time (s)");
ylabel("bytes");

subplot(3,1,3);
plot(index(1:1000), bytesp)
title("Video Arrival");
xlabel("Sequence Number");
ylabel("bytes");

figure(2);
subplot(2,1,1);
plot(timesb ./1000000, bufferSize(1:length(timesb)))
title("Buffer Size");
xlabel("time (s)");
ylabel("bytes");

subplot(2,1,2);
plot(timesb ./1000000, tokens(1:length(timesb)))
title("Contents of Token Bucket (no. tokens)");
xlabel("time (s)");
ylabel("Tokens");

total_sender_10K = bytes_sump
total_sink_10K = bytes_sum
