import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        String file = readFile("sample01.vtt");
        String[] data = file.split("\n");
        ArrayList<eachExcerpt> excerptArray = createData(data); // turns data into readable data

       createCondensedTranscript(excerptArray,"condensedTranscript.txt");

       createSummaryStatistics(excerptArray, "summaryStatistics.txt");
    }

    private static void createSummaryStatistics(ArrayList<eachExcerpt> arr, String fileName) {
        String contents = "Summary statistics file:\n\n";
        HashMap<String, Double> participantsTimes = createParticipantsStats(arr);
        HashMap<String, Double> averageSpeakingTimes = createSpeakingTimes(arr);
            contents +=
                "Total # people: " + participantsTimes.size() +
                "\nTotal length of session: " + totalSessionTime(arr) + " min" +
                "\nTotal # of speaker switches: " + getNumSwitches(arr) +
                "\n\nTotal talk time\n";
            for (String name : participantsTimes.keySet()) {
                contents += "\n" + name + ": " + participantsTimes.get(name) + " min";
            }
            contents +=
                    "\n\nAverage Speaking Time \n";
            for (String name : averageSpeakingTimes.keySet()) {
                contents += "\n" + name + ": " + averageSpeakingTimes.get(name) + " sec";
            }
        writeDataToFile(fileName,contents);
    }

    private static HashMap<String, Double> createSpeakingTimes(ArrayList<eachExcerpt> arr) {
        HashMap<String, Double> speakingTimes = createParticipantsHM(arr);
        for (int i = 0; i < arr.size(); i++) {
            String speaker = arr.get(i).getSpeaker();
            double avgSpeakingTimes = Math.round(getAvgSpeakingTimes(arr, speaker)*100)/100.0;
            speakingTimes.put(speaker,avgSpeakingTimes);
        } return speakingTimes;
    }

    private static double getAvgSpeakingTimes(ArrayList<eachExcerpt> arr, String speaker) {
        int counter = 0;
        double sum = 0;
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).getSpeaker().equals(speaker)) {
                counter++;
                sum +=arr.get(i).getTimeDiff();
            }
        } return sum/counter;
    }

    private static HashMap<String, Double> createParticipantsStats(ArrayList<eachExcerpt> arr) {
        HashMap<String, Double> participantsTimes = createParticipantsHM(arr);
        for (int i = 0; i < arr.size(); i++) {
            String currSpeaker = arr.get(i).getSpeaker();
            double currTime = arr.get(i).getTimeDiff();
            double updatedTime = participantsTimes.get(currSpeaker) + currTime;
            participantsTimes.put(currSpeaker,updatedTime);
        }
        convertIntoMins(participantsTimes);
        return participantsTimes;
    }

    private static HashMap<String, Double> convertIntoMins(HashMap<String, Double> hm) {
        for (String name : hm.keySet()) {
            double mins = Math.round((hm.get(name)/60.0)*100)/100.0;
            hm.put(name, mins);
        } return hm;
    }

    private static HashMap<String, Double> createParticipantsHM(ArrayList<eachExcerpt> arr) {
        HashMap<String, Double> participantsTimes = new HashMap<>();
        ArrayList<String> participantsList = getParticipants(arr);
        for (int i = 0; i < participantsList.size(); i++) {
            participantsTimes.put(participantsList.get(i),0.0);
        } return participantsTimes;
    }

    private static double totalSessionTime(ArrayList<eachExcerpt> arr) {
        eachExcerpt lastIndex = arr.get(arr.size()-1);
        double minutesSinceStart = lastIndex.timeSinceStart()/60;
        return Math.round(minutesSinceStart*100)/100.0;
    }

    private static void createCondensedTranscript(ArrayList<eachExcerpt> arr, String fileName) {
        String contents = "Condensed transcript file:\n\n";
        for (int i = 1; i < arr.size(); i++) {
            double sum = 0;
            sum += arr.get(i-1).getTimeDiff();
            while(i < arr.size()-1 && arr.get(i-1).getSpeaker().equals(arr.get(i).getSpeaker())) {
                sum += arr.get(i).getTimeDiff();
                i++;
            }
            double roundedSum = Math.round(sum*100)/100.0;
            contents += arr.get(i-1).getSpeaker() + ": " + roundedSum + " sec\n";
        }
        writeDataToFile(fileName, contents);
    }

    private static ArrayList<eachExcerpt> createData(String[] data) {
        ArrayList<eachExcerpt> arr = new ArrayList<>();
        String time, speaker="", words="";
        for (int i = 2; i < data.length; i++) {
            time = "";
            if ((i-2)%4 == 1) {
                time = data[i];
                int nextLine = i+1;
                int loc = data[nextLine].indexOf(":");
                if (loc >= 0) {
                    speaker = data[nextLine].substring(0, loc);
                    words = data[nextLine].substring(loc + 2);
                }
            }
            if (!time.equals("")) arr.add(new eachExcerpt(time, speaker, words));
        } return arr;
    }

    private static int getNumSwitches(ArrayList<eachExcerpt> arr) {
        int counter = 0;
        for (int i = 0; i < arr.size()-1; i++) {
            String currentSpeaker = arr.get(i).getSpeaker();
            String nextSpeaker = arr.get(i+1).getSpeaker();
            if (!currentSpeaker.equals(nextSpeaker)) counter++;
        } return counter;
    }
    private static ArrayList<String> getParticipants(ArrayList<eachExcerpt> arr) {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            String currName = arr.get(i).getSpeaker();
            if (isUnique(names, currName)) names.add(currName);
        } return names;
    }

    private static boolean isUnique(ArrayList<String> arr, String currName) {
        for (String instance : arr) {
            if (instance.equals(currName)) {
                return false;
            }
        } return true;
    }

    public static void writeDataToFile(String filePath, String data) {
        try (FileWriter f = new FileWriter(filePath);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter writer = new PrintWriter(b);) {


            writer.println(data);


        } catch (IOException error) {
            System.err.println("There was a problem writing to the file: " + filePath);
            error.printStackTrace();
        }
    }

    public static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

}
