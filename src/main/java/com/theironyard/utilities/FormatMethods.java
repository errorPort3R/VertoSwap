package com.theironyard.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by jeffryporter on 7/27/16.
 */
public class FormatMethods
{
    public static String timeDateFormatter(LocalDateTime incomingLDT)
    {
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("MM/dd-hh:mm");
        return dTF.format(incomingLDT);
    }
}
