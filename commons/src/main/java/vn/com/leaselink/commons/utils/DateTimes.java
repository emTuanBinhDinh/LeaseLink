//package vn.com.leaselink.commons.utils;
//
//import org.joda.time.*;
//import vn.com.leaselink.commons.adapter.LogAdapter;
//
//import java.sql.Time;
//import java.sql.Timestamp;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//public class DateTimes {
////    private static final LogAdapter LOGGER = LogAdapter.newInstance(DateTimes.class);
//
//    public static Date getCurrentDate() {
//        return new Date(System.currentTimeMillis());
//    }
//
//    public static java.sql.Date getSqlCurrentDate() {
//        return new java.sql.Date(System.currentTimeMillis());
//    }
//
//    public static java.sql.Date parseDateToSqlDate(Date date) {
//        return new java.sql.Date(date.getTime());
//    }
//
//    public static java.sql.Date parseDateToSqlDate(Long date) {
//        return new java.sql.Date(date);
//    }
//
//    public static Timestamp getCurrentTimeStamp() {
//        return new Timestamp(System.currentTimeMillis());
//    }
//
//    public static Timestamp getTimeStamp(Long data) {
//        return new Timestamp(data);
//    }
//
//    public static Long getCurrentTimeMillis() {
//        return System.currentTimeMillis();
//    }
//
//    public static Date getCurrentDateMillis() {
//        Date date = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTime();
//    }
//
//    public static Time getCurrentTimes() {
//        return new Time(System.currentTimeMillis());
//    }
//
//    public static LocalDateTime getCurrentLocalDateTime() {
//        return LocalDateTime.now();
//    }
//
//    public static LocalDate getCurrentLocalDate() {
//        return LocalDate.now();
//    }
//
//    public static DateTime getDateTime(Long time) {
//        return new DateTime(time);
//    }
//
//    public static Date getTomorrow() {
//        Date date = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        c.add(Calendar.DATE, 1);
//        return c.getTime();
//    }
//
//    public static Date getTomorrow(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        c.add(Calendar.DATE, 1);
//        return c.getTime();
//    }
//
//    public static Timestamp parseTimestamp(String timeString, String pattern) {
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//        try {
//            Date date = format.parse(timeString);
//            return new Timestamp(date.getTime());
//        } catch (ParseException e) {
//            LOGGER.error("Parse timestamp {} failed cause by {}", timeString, e.getMessage());
//        }
//        return null;
//    }
//
//    public static Date parseDate(String dateStr, String pattern) {
//        return parseTimestamp(dateStr, pattern);
//    }
//
//    public static Timestamp timestampAdd(Timestamp timestamp, Long interval) {
//        return new Timestamp(timestamp.getTime() + interval);
//    }
//
//    public static Date dateAdd(Date date, Long interval) {
//        return new Date(date.getTime() + interval);
//    }
//
//    public static Long dateAdd(Long date, Long interval) {
//        return date + interval;
//    }
//
//    public static java.sql.Date parseLongToDate(Long milis) {
//        return new java.sql.Date(milis);
//    }
//
//    public static Date getDateLastMonth(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        date = toEndTimeOfDay(date);
//        calendar.setTime(date);
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        return calendar.getTime();
//    }
//
//    public static Long getTimeFirstMonth(Long time) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(time);
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
//        return calendar.getTimeInMillis();
//    }
//
//    public static Long getTimeLastMonth(Long time) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(time);
//        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        return calendar.getTimeInMillis();
//    }
//
//    public static Long getTimeAfterMonth(Long time, Integer month) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(time);
//        calendar.add(Calendar.MONTH, month);
//        return calendar.getTimeInMillis();
//    }
//
//    public static Long minusDateToDay(Long start, Long end) {
//        TimeUnit timeUnit = TimeUnit.DAYS;
//        long diffInMillis = end - start;
//        return timeUnit.convert(diffInMillis, TimeUnit.MILLISECONDS);
//    }
//
//    public static Date minusDays(Date date, Integer i) {
//        return new DateTime(date).minusDays(i).toDate();
//    }
//
//    public static Date plusDays(Date date, Integer i) {
//        return new DateTime(date).plusDays(i).toDate();
//    }
//
//    public static Date plusWeeks(Date date, Integer i) {
//        return new DateTime(date).plusWeeks(i).toDate();
//    }
//
//    public static Date plusMonths(Date date, Integer i) {
//        return new DateTime(date).plusMonths(i).toDate();
//    }
//
//    public static Date getFirstDateOfMonth(Date date) {
//        Calendar c = Calendar.getInstance();   // this takes current date
//        date = toBeginTimeOfDay(date);
//        c.setTime(date);
//        c.set(Calendar.DAY_OF_MONTH, 1);
//        return c.getTime();
//    }
//
//    public static Date getEndDateOfYear(Date date) {
//        Calendar c = Calendar.getInstance();
//        date = toEndTimeOfDay(date);
//        c.setTime(date);
//        c.add(Calendar.YEAR, 1);
//        c.set(Calendar.DAY_OF_YEAR, 1);
//        c.add(Calendar.DAY_OF_YEAR, -1);
//        return c.getTime();
//    }
//
//    public static Date getLastDayOfQuarter(Date date) {
//        Calendar cal = Calendar.getInstance();
//        date = toEndTimeOfDay(date);
//        cal.setTime(date);
//        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3 + 2);
//        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//        return cal.getTime();
//    }
//
//    public static Date getFirstDayOfQuarter(Date date) {
//        Calendar cal = Calendar.getInstance();
//        date = toBeginTimeOfDay(date);
//        cal.setTime(date);
//        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        return cal.getTime();
//    }
//
//    public static Date getFirstDateOfYear(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        date = toBeginTimeOfDay(date);
//        calendar.setTime(date);
//        calendar.set(Calendar.DAY_OF_YEAR, 1);
//        return calendar.getTime();
//    }
//
//    public static Long getBeginTimeOfDay() {
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTimeInMillis();
//    }
//
//    public static Date toBeginTimeOfDay(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTime();
//    }
//
//    public static Date toEndTimeOfDay(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 23);
//        c.set(Calendar.MINUTE, 59);
//        c.set(Calendar.SECOND, 59);
//        c.set(Calendar.MILLISECOND, 999);
//        return c.getTime();
//    }
//
//    public static Date toBeginTimeOfDay(Long date) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTime();
//    }
//
//    public static Long toBeginTimeOfDayInMillis(Long date) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTimeInMillis();
//    }
//
//    public static Date toEndTimeOfDay(Long date) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(date);
//        c.set(Calendar.HOUR_OF_DAY, 23);
//        c.set(Calendar.MINUTE, 59);
//        c.set(Calendar.SECOND, 59);
//        c.set(Calendar.MILLISECOND, 999);
//        return c.getTime();
//    }
//
//    public static Date getFirstDateOfWeek(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        date = toBeginTimeOfDay(date);
//        calendar.setTime(date);
//        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//        return calendar.getTime();
//    }
//
//    public static Date getLastDateOfWeek(Date date) {
//        Calendar calendar = Calendar.getInstance();
//        date = toBeginTimeOfDay(date);
//        calendar.setTime(date);
//        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//        calendar.add(Calendar.WEEK_OF_YEAR, 1);
//        calendar.add(Calendar.DAY_OF_YEAR, -1);
//        return calendar.getTime();
//    }
//
//    public static List<Long> getDaysBetweenDates(Long beginDate, Long endDate) {
//        List<Long> dates = new ArrayList<Long>();
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTimeInMillis(beginDate);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        while (calendar.getTime().before(new Date(endDate)) || calendar.getTime().equals(new Date(endDate))) {
//            Long result = calendar.getTimeInMillis();
//            dates.add(result);
//            calendar.add(Calendar.DATE, 1);
//        }
//        return dates;
//    }
//
//    public static Integer getDateNow() {
//        Calendar cal = Calendar.getInstance();
//        return cal.get(Calendar.DATE) + 1;
//    }
//
//    public static Integer getMonthNow() {
//        Calendar cal = Calendar.getInstance();
//        return cal.get(Calendar.MONTH) + 1;
//    }
//
//    public static Long addDate(Long date, Integer number, Integer unit) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(date);
//        c.add(unit, number);
//        return c.getTimeInMillis();
//    }
//
//    public static Date addDateFromNow(Integer number, Integer unit) {
//        Calendar c = Calendar.getInstance();
//        c.add(unit, number);
//        return c.getTime();
//    }
//
//    public static Integer dayBetween(Date from, Date to) {
//        DateTime a = new DateTime(from.getTime());
//        DateTime b = new DateTime(to.getTime());
//        return Days.daysBetween(a, b).getDays();
//    }
//
//    public static Integer weekBetween(Date from, Date to) {
//        DateTime a = new DateTime(from.getTime());
//        DateTime b = new DateTime(to.getTime());
//        return Weeks.weeksBetween(a, b).getWeeks();
//    }
//
//    public static Integer monthBetween(Date from, Date to) {
//        DateTime a = new DateTime(from.getTime());
//        DateTime b = new DateTime(to.getTime());
//        return Months.monthsBetween(a, b).getMonths();
//    }
//
//    public static Integer monthBetween(Long from, Long to) {
//        DateTime a = new DateTime(from);
//        DateTime b = new DateTime(to);
//        return Months.monthsBetween(a, b).getMonths();
//    }
//
//    public static Integer yearBetween(Date from, Date to) {
//        DateTime a = new DateTime(from.getTime());
//        DateTime b = new DateTime(to.getTime());
//        return Years.yearsBetween(a, b).getYears();
//    }
//
//    public static Integer yearBetween(Long from, Long to) {
//        DateTime a = new DateTime(from);
//        DateTime b = new DateTime(to);
//        return Years.yearsBetween(a, b).getYears();
//    }
//
//    public static Long getBeginTimeOfDay(Long date) {
//        Calendar c = Calendar.getInstance();
//        Date d = new Date(date);
//        c.setTime(d);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return c.getTimeInMillis();
//    }
//
//    public static Integer dayBetween(Long from, Long to) {
//        return dayBetween(new Date(from), new Date(to));
//    }
//
//    public static Integer weekBetween(Long from, Long to) {
//        return weekBetween(new Date(from), new Date(to));
//    }
//
//    public static java.sql.Date toBeginTimeOfDaySql(Long date) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        return new java.sql.Date(c.getTimeInMillis());
//    }
//
//    public static java.sql.Date toEndTimeOfDaySql(Long date) {
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(date);
//        c.set(Calendar.HOUR_OF_DAY, 23);
//        c.set(Calendar.MINUTE, 59);
//        c.set(Calendar.SECOND, 59);
//        c.set(Calendar.MILLISECOND, 999);
//        return new java.sql.Date(c.getTimeInMillis());
//    }
//
//    public static void checkExpiredTime(Timestamp timestamp) {
//        if (timestamp.before(getCurrentDate()))
//            throw new CommonException.TimeoutException("Timeout");
//    }
//
//    public static void checkExpiredTime(Long timeToCheck) throws CommonException.TimeoutException {
//        if (timeToCheck < getCurrentTimeMillis()) {
//            throw new CommonException.TimeoutException("Timeout");
//        }
//    }
//
//    public static Long parseTimeByPattern(String pattern, String time) {
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//        try {
//            return format.parse(time).getTime();
//        } catch (ParseException e) {
//            throw new CommonException.UnsupportException("Time not match with format");
//        }
//    }
//
//    public static void checkExpiredTime(Timestamp timeToCheck, Long timeToLive) {
//        Timestamp temp = new Timestamp(timeToCheck.getTime() + timeToLive);
//        if (temp.before(getCurrentDate())) {
//            throw new CommonException.TimeoutException("Timeout");
//        }
//    }
//
//    public static void checkExpiredTime(Long timeToCheck, Long timeToLive) {
//        Timestamp timeCheck = new Timestamp(timeToCheck + timeToLive);
//        if (timeCheck.before(getCurrentDate())) {
//            throw new CommonException.TimeoutException("Timeout");
//        }
//    }
//
//    public static void checkFromToDate(Long fromDateInMillis, Long toDateInMillis) {
//        if (fromDateInMillis > getCurrentTimeMillis()) {
//            throw new CommonException.TimeoutException("From date is less than now");
//        }
//        if (toDateInMillis < getCurrentTimeMillis()) {
//            throw new CommonException.TimeoutException("To date is greater than now");
//        }
//    }
//
//    public static String getCurrentDateByDDMMYYYY() {
//        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date());
//    }
//
//    public static String getCurrentDateByPattern(String pattern) {
//        return new SimpleDateFormat(pattern).format(new Date());
//    }
//
//    public static String parseLocalDateTimeByPattern(LocalDateTime time, String pattern) {
//        return new SimpleDateFormat(pattern).format(Timestamp.valueOf(time));
//    }
//
//    public static String parseLocalDateByPattern(LocalDate date, String pattern) {
//        return new SimpleDateFormat(pattern).format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
//    }
//
//    public static LocalDateTime parseLocalDateTimeByPattern(String timeString, String pattern) {
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//        try {
//            Date date = format.parse(timeString);
//            return getLocalDateTime(date.getTime());
//        } catch (ParseException e) {
//            LOGGER.error("Parse timestamp {} failed cause by {}", timeString, e.getMessage());
//        }
//        return null;
//    }
//
//    public static LocalDate parseLocalDateByPattern(String timeString, String pattern) {
//        SimpleDateFormat format = new SimpleDateFormat(pattern);
//        try {
//            Date date = format.parse(timeString);
//            return getLocalDate(date.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//            LOGGER.error("Parse timestamp {} failed cause by {}", timeString, e.getMessage());
//        }
//        return null;
//    }
//
//    public static Long convert(TimeUnit to, TimeUnit from, Integer value) {
//        switch (from) {
//            case DAYS:
//                return to.toDays(value);
//            case HOURS:
//                return to.toHours(value);
//            case MINUTES:
//                return to.toMinutes(value);
//            case SECONDS:
//                return to.toSeconds(value);
//            case MILLISECONDS:
//                return to.toMillis(value);
//            case MICROSECONDS:
//                return to.toMillis(value);
//            default:
//                throw new IllegalArgumentException("Un-support time unit: " + from);
//        }
//    }
//
//    public static Date getYesterday() {
//        Date date = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        c.add(Calendar.DATE, -1);
//        return c.getTime();
//    }
//
//    public static Date getYesterday(Date date) {
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.set(Calendar.HOUR_OF_DAY, 0);
//        c.set(Calendar.MINUTE, 0);
//        c.set(Calendar.SECOND, 0);
//        c.set(Calendar.MILLISECOND, 0);
//        c.add(Calendar.DATE, -1);
//        return c.getTime();
//    }
//
//    public static LocalDate getLocalDate(Long date) {
//        return java.time.Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate();
//    }
//
//    public static Long getLocalDate(LocalDate date) {
//        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
//    }
//
//    public static Long getLocalDateTime(LocalDateTime dateTime) {
//        ZonedDateTime zdt = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
//        return zdt.toInstant().toEpochMilli();
//    }
//
//    public static LocalDateTime getLocalDateTime(Long datetime) {
//        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(datetime),
//                TimeZone.getDefault().toZoneId());
//    }
//
//    public static LocalDateTime atEndOfDay(Long datetime) {
//        return LocalDateTime.of(getLocalDate(datetime), java.time.LocalTime.MAX);
//    }
//
//    public static LocalDateTime atEndOfDay(LocalDate date) {
//        return LocalDateTime.of(date, LocalTime.MAX);
//    }
//}
