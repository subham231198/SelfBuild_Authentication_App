package com.Banking.SelfBuild.Self.Build.Utility;

import java.util.Random;

public class OtpGenerator
{
    public String otpGenerator()
    {
        Random random = new Random();
        int number = random.nextInt(1_000_000);
        return String.format("%06d", number);
    }
}
