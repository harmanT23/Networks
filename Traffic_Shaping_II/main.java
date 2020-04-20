import TokenBucket.TokenBucket;

public class main {
	public static void main(String[] args) { 
	// listen on port 4444, send to localhost:4443, 
	// max. size of received packet is 1024 bytes, 
	// buffer capacity is 100*1024 bytes, 
	// token bucket has 10000 tokens, rate 5000 tokens/sec, and 
	// records packet arrivals to bucket.txt).

	TokenBucket lb = new TokenBucket(4444, //inport
									"localhost", //outaddress
									4443, //outport
									1520, //Max packet size in bytes
									45000, //Buffer capacity in bytes
									220000, //Bucket Size in bytes
									183750, //Tokens per second
									"bucket.txt"); //File to record arrivals
	new Thread(lb).start();
	}
}