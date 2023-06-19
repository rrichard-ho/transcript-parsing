public class eachExcerpt {
    private String time, words, speaker;
    private String[] firstSegment, secondSegment;

    public eachExcerpt(String time, String speaker, String words) {
        this.time = time;
        this.speaker = speaker;
        this.words = words;

    }

    public String getTime() {
        return time;
    }

    public String getWords() {
        return words;
    }

    public String getSpeaker() {
        return speaker;
    }

    public double getTimeDiff() {
        String[] segments = time.split(" --> ");
        firstSegment = segments[0].split(":");
        secondSegment = segments[1].split(":");

        return calculateSeconds(firstSegment,secondSegment);
    }

    public double timeSinceStart() {
        double hourTwo = Double.parseDouble(secondSegment[0]) * 3600;
        double minuteTwo = Double.parseDouble(secondSegment[1]) * 60;
        double secondTwo = Double.parseDouble(secondSegment[2]);
        return hourTwo + minuteTwo + secondTwo;
    }

    private double calculateSeconds(String[] firstSegment, String[] secondSegment) {
        double hourOne = Double.parseDouble(firstSegment[0]) * 3600;
        double minuteOne = Double.parseDouble(firstSegment[1]) * 60;
        double secondOne = Double.parseDouble(firstSegment[2]);
        double timeOne = hourOne + minuteOne + secondOne;

        double hourTwo = Double.parseDouble(secondSegment[0]) * 3600;
        double minuteTwo = Double.parseDouble(secondSegment[1]) * 60;
        double secondTwo = Double.parseDouble(secondSegment[2]);
        double timeTwo = hourTwo + minuteTwo + secondTwo;
        double timeDiff = timeTwo-timeOne;
        return Math.round(timeDiff*1000)/1000.0;
    }
}
