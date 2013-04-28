Time
====

This project tries to solve several time-related problems:

1. We would like to get the current time at a higher precision then milliseconds.
2. We would like the current time/date to be formated in UTC/GMT by default.
3. We would like to be able to rely on the fact that the clock *never goes backward*.
4. We would like a lightweight task scheduling system based on the previously defined time.
5. We would like a highly precise (0.1 millesecond or better) "ticker" clock, for real-time games.
6. We would like for all this to still work, in the presence of a bad (wrong time, wrong time-zone) system clock.
7. We would like for all this to still work, independent of the fact that the host gets synchronized or not.
8. Building on top of the accurate UTC time, we would like to get an accurate local time too.

Note that we need a correct time zone to get the correct local time. There is no reliable way to discover the local timezone.

This, as you can imagine, requires that we go out in the Internet, and query what the real time is, independent of what the host think it is. This all grew from the realisation, that a large part of our users have totally wrong system time, and we need accurate system time for our operation. So the use of this API relieves the user from the burden of making sure the local system time is correct. We do expect that the user will at least get their own time-zone right, if they want accurate local time.

This project should be OSGi-ready, uses Google Guice, and depends on the JSR 310 backport. Eventually, the JSR 310 implementation in Java 8 will also be supported.

The implementation has been moved to a separate project: TimeImpl
https://github.com/skunkiferous/TimeImpl
