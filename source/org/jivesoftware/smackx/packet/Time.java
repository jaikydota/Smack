package org.jivesoftware.smackx.packet;

import org.jivesoftware.smack.packet.IQ;

import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

/**
 * A Time IQ packet, which is used by XMPP clients to exchange their respective local
 * times. Clients that wish to fully support the entitity time protocol should register
 * a PacketListener for incoming time requests that then respond with the local time.
 * This class can be used to request the time from other clients, such as in the
 * following code snippet:
 *
 * <pre>
 * <font color="darkgreen">// Request the time from a remote user.</font>
 * Time timeRequest = <font color="navy"><b>new</b></font> Time();
 * timeRequest.setType(IQ.Type.GET);
 * timeRequest.setTo(<font color="red">"someUser@example.com"</font>);

 * &nbsp;
 * <font color="darkgreen">// Create a packet collector to listen for a response.</font>
 * PacketCollector collector = con.createPacketCollector(
 *                <font color="navy"><b>new</b></font> PacketIDFilter(timeRequest.getPacketID()));
 * &nbsp;
 * con.sendPacket(timeRequest);
 * &nbsp;
 * <font color="darkgreen">// Wait up to 5 seconds for a result.</font>
 * IQ result = (IQ)collector.nextResult(5000);
 * <font color="navy"><b>if</b></font> (result != <font color="navy"><b>null</b></font> &#38;&#38; result.getType() == IQ.Type.RESULT) <font color="navy">{</font>
 *     Time timeResult = (Time)result;
 *     <font color="darkgreen">// Do something with result...</font>
 * <font color="navy">}</font></pre><p>
 *
 * Warning: this is an non-standard protocol documented by
 * <a href="http://www.jabber.org/jeps/jep-0090.html">JEP-90</a>. Because this is a
 * non-standard protocol, it is subject to change.
 *
 * @author Matt Tucker
 */
public class Time extends IQ {

    private static SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd'T'hh:mm:ss");
    private static DateFormat displayFormat = DateFormat.getDateTimeInstance();

    private String utc = null;
    private String tz = null;
    private String display = null;

    /**
     * Creates a new Time instance with empty values for all fields.
     */
    public Time() {
        this(Calendar.getInstance());
    }

    /**
     * Cretaes a new Time instance using the specified calendar instance as
     * the time value to send.
     *
     * @param cal the time value.
     */
    public Time(Calendar cal) {
        TimeZone timeZone = cal.getTimeZone();
        tz = cal.getTimeZone().getID();
        display = displayFormat.format(cal.getTime());
        // Convert local time to the UTC time.
        utc = utcFormat.format(new Date(
                cal.getTimeInMillis() - timeZone.getOffset(cal.getTimeInMillis())));
    }

    /**
     * Returns the local time or <tt>null</tt> if the time hasn't been set.
     *
     * @return the lcocal time.
     */
    public Date getTime() {
        if (utc == null) {
            return null;
        }
        Date date = null;
        try {
            Calendar cal = Calendar.getInstance();
            // Convert the UTC time to local time.
            cal.setTime(new Date(utcFormat.parse(utc).getTime() +
                    cal.getTimeZone().getOffset(cal.getTimeInMillis())));
            date = cal.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Sets the time using the local time.
     *
     * @param time the current local time.
     */
    public void setTime(Date time) {
        // Convert local time to UTC time.
        utc = utcFormat.format(new Date(
                time.getTime() - TimeZone.getDefault().getOffset(time.getTime())));
    }

    /**
     * Returns the time as a UTC formatted String using the format CCYYMMDDThh:mm:ss.
     *
     * @return the time as a UTC formatted String.
     */
    public String getUtc() {
        return utc;
    }

    /**
     * Sets the time using UTC formatted String in the format CCYYMMDDThh:mm:ss.
     *
     * @param utc the time using a formatted String.
     */
    public void setUtc(String utc) {
        this.utc = utc;

    }

    /**
     * Returns the time zone.
     *
     * @return the time zone.
     */
    public String getTz() {
        return tz;
    }

    /**
     * Sets the time zone.
     *
     * @param tz the time zone.
     */
    public void setTz(String tz) {
        this.tz = tz;
    }

    /**
     * Returns the local (non-utc) time in human-friendly format.
     *
     * @return the local time in human-friendly format.
     */
    public String getDisplay() {
        return display;
    }

    /**
     * Sets the local time in human-friendly format.
     *
     * @param display the local time in human-friendly format.
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    public String getChildElementXML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<query xmlns=\"jabber:iq:time\">");
        if (utc != null) {
            buf.append("<utc>").append(utc).append("</utc>");
        }
        if (tz != null) {
            buf.append("<tz>").append(tz).append("</tz>");
        }
        if (display != null) {
            buf.append("<display>").append(display).append("</display>");
        }
        buf.append("</query>");
        return buf.toString();
    }
}