clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading the data and putting the first 1000000 entries in variables 
%Note that time is in seconds and framesize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
no_entries=1000000;
[time1, framesize1] = textread('Bel.data', '%f %f');
time=time1(1:no_entries);
framesize=framesize1(1:no_entries);

bytes_total=sum(framesize)

A=time; 
peak_bit_rate=-1;

%Get peak bit rate
sum1=0;
for i=1:length(A)-1
  sum1= sum1 + (A(i+1)-A(i));
   if peak_bit_rate < (framesize(i+1)/(A(i+1)-A(i)))
       peak_bit_rate = (framesize(i+1)/(A(i+1)-A(i)));
   end
end

%Compute mean/peak bit rates and ratio and other properties
mean_bit_rate = sum(framesize)/time(end)*8
peak_bit_rate = peak_bit_rate * 8
peak_average_ratio = peak_bit_rate / mean_bit_rate

mean_fs = mean(framesize)
max_frame_size = max(framesize)

figure(1);
plot(time,framesize);
title("Packet size over time");
xlabel("time (sec)");
ylabel("Packet size (Bytes)");

figure(2);
histogram(framesize);
title("Distribution of packets");
xlabel("Packet size (Bytes)");
ylabel("Relative frequency");
 
 %%%%%%%%%%%%%%%%%%%%%%%%%Exercise %%%3.2%%%%%%%%%%%%%%%%%%%%%%%%%%%
 %The following code will generate Plot 1; You generate Plot2 , Plot3.
 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 no_entries=1000000;
 figure(3);
 jj=1;
 i=1;
 initial_p=0;
 ag_time=1;
 bytes_p=zeros(1,100);
 while time(jj)<=initial_p
     jj=jj+1;
 end
 while i<=100
 while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)
 bytes_p(i)=bytes_p(i)+framesize(jj);
 jj=jj+1;
 end
 i=i+1;
 end
 %%%%%%%%
 subplot(3,1,1);bar(bytes_p);
 title("Plot1: 100 elements storing data within 1s interval");
 xlabel("time (sec)");
 ylabel("Packet size (Bytes)");
 
 
 jj=1;
 i=1;
 initial_p=20;
 ag_time=0.1;
 bytes_p=zeros(1,100);
 while time(jj)<=initial_p
     jj=jj+1;
 end
 while i<=100
 while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)
 bytes_p(i)=bytes_p(i)+framesize(jj);
 jj=jj+1;
 end
 i=i+1;
 end
 %%%%%%%%
 x=20.1:0.1:30;
 subplot(3,1,2);bar(x,bytes_p);
 title("Plot2: 100 elements storing data within 100ms interval");
 xlabel("time (sec)");
 ylabel("Packet size (Bytes)");
 
 
 jj=1;
 i=1;
 initial_p=90;
 ag_time=0.01;
 bytes_p=zeros(1,100);
 while time(jj)<=initial_p
     jj=jj+1;
 end
 while i<=100
 while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)
 bytes_p(i)=bytes_p(i)+framesize(jj);
 jj=jj+1;
 end
 i=i+1;
 end
 %%%%%%%%
 x=90.01:0.01:91;
 subplot(3,1,3);bar(x,bytes_p);
 title("Plot3: 100 elements storing data within 10ms interval");
 xlabel("time (sec)");
 ylabel("Packet size (Bytes)");



