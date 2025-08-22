import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FormData {
    private static final DateTimeFormatter DOB_FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private final boolean changeNames;
    private final String civilFirstNames;
    private final String chosenFirstNames;
    private final String lastName;
    private final String birthDate;
    private final String birthPlace;
    private final String civilSex;
    private final String address;
    private final String tribunal;
    private final String narrative;
    private final String currentCity;
    private final List<String> attachments;
    private final String nationality;
    private final String profession;
    private final String maritalStatus;
    private final String childrenInfo;
    private final boolean pacsContracted;

    public FormData(boolean changeNames,
                    String civilFirstNames,
                    String chosenFirstNames,
                    String lastName,
                    String birthDate,
                    String birthPlace,
                    String civilSex,
                    String address,
                    String tribunal,
                    String narrative,
                    String currentCity,
                    List<String> attachments,
                    String nationality,
                    String profession,
                    String maritalStatus,
                    String childrenInfo,
                    boolean pacsContracted) {
        this.changeNames = changeNames;
        this.civilFirstNames = civilFirstNames;
        this.chosenFirstNames = chosenFirstNames;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.civilSex = civilSex;
        this.address = address;
        this.tribunal = tribunal;
        this.narrative = narrative;
        this.currentCity = currentCity;
        this.attachments = attachments == null ? List.of() : List.copyOf(attachments);
        this.nationality = nationality == null ? "" : nationality;
        this.profession = profession == null ? "" : profession;
        this.maritalStatus = maritalStatus == null ? "" : maritalStatus;
        this.childrenInfo = childrenInfo == null ? "" : childrenInfo;
        this.pacsContracted = pacsContracted;
    }

    public boolean isChangeNames() {
        return changeNames;
    }

    public String getCivilFirstNames() {
        return civilFirstNames;
    }

    public String getChosenFirstNames() {
        return chosenFirstNames;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLastNameUpper() {
        return lastName == null ? "" : lastName.toUpperCase(Locale.ROOT);
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getCivilSex() {
        return civilSex;
    }

    public String getAddress() {
        return address;
    }

    public String getTribunal() {
        return tribunal;
    }

    public String getNarrative() {
        return narrative;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public List<String> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public String getNationality() {
        return nationality;
    }

    public String getProfession() {
        return profession;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getChildrenInfo() {
        return childrenInfo;
    }

    public boolean isPacsContracted() {
        return pacsContracted;
    }

    public String getTargetSex() {
        if (civilSex == null) return "";
        String s = civilSex.trim().toLowerCase(Locale.ROOT);
        if (s.startsWith("m")) return "Féminin";
        if (s.startsWith("f")) return "Masculin";
        return "";
    }

    public String getAge() {
        try {
            LocalDate dob = LocalDate.parse(birthDate, DOB_FMT);
            LocalDate today = LocalDate.now();
            int years = today.getYear() - dob.getYear();
            if (today.getDayOfYear() < dob.getDayOfYear()) years--;
            return Integer.toString(years);
        } catch (Exception ex) {
            return "";
        }
    }

    public boolean isTargetFeminine() {
        String t = getTargetSex();
        return t != null && t.toLowerCase(Locale.ROOT).startsWith("f");
    }

    public String honorificTarget() {
        return isTargetFeminine() ? "Madame" : "Monsieur";
    }

    public String honorificCivil() {
        String s = civilSex == null ? "" : civilSex.trim().toLowerCase(Locale.ROOT);
        return s.startsWith("f") ? "Madame" : "Monsieur";
    }

    public String reqNoun() {
        return isTargetFeminine() ? "la requérante" : "le requérant";
    }

    public String reqNounCapitalized() {
        return isTargetFeminine() ? "La requérante" : "Le requérant";
    }

    public String adjAge() {
        return isTargetFeminine() ? "âgée" : "âgé";
    }

    public String adjBorn() {
        return isTargetFeminine() ? "née" : "né";
    }

    public String adjInscrit() {
        return isTargetFeminine() ? "inscrite" : "inscrit";
    }

    public String sousSigne() {
        return isTargetFeminine() ? "soussignée" : "soussigné";
    }

    public String targetGenderNoun() {
        return isTargetFeminine() ? "femme" : "homme";
    }

    public String targetGenderAdj() {
        return isTargetFeminine() ? "féminin" : "masculin";
    }

    public String civilGenderAdj() {
        String s = civilSex == null ? "" : civilSex.trim().toLowerCase(Locale.ROOT);
        return s.startsWith("f") ? "féminin" : "masculin";
    }

    public String pronounIlElle() {
        return isTargetFeminine() ? "elle" : "il";
    }

    public String pronounQuIlElle() {
        return isTargetFeminine() ? "qu’elle" : "qu’il";
    }

    public String adjConnuConnue() {
        return isTargetFeminine() ? "connue" : "connu";
    }
}