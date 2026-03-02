package com.Banking.SelfBuild.Self.Build;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SelfBuildApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(SelfBuildApplication.class, args);
	}
}
