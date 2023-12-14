package com.snhu.sslserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class SslServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SslServerApplication.class, args);
	}

}

@RestController
class ServerController {
	
	// The following code is nearly identical to the code submitted for 5-1 Coding Assignment. This is because that assignment was used as a rubric to build out this code block.
	@RequestMapping("/hash")
	public String hash() {
		String data = "Hello World Check Sum!";
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte [] hash = md.digest(data.getBytes());
		String hexHash = bytesToHex(hash);
		
		return "<p>data: "+data+"</p>"
		+ "<p>Algorithm used: "+md.getAlgorithm()+"</p>"
		+ "<p>Checksum Value: "+hexHash;
	}

	// bytesToHex Function implementation
    private String bytesToHex(byte[] bytes) {
    	StringBuilder output = new StringBuilder();
    	for (byte b : bytes) {
    		output.append(String.format("%02x", b));
    	}
    	return output.toString();
    }
}