import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class DocumentGenerator {
    private static final DateTimeFormatter TODAY_FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter DOB_IN_FMT = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter DOB_OUT_FMT = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.FRENCH);
    private static final String NOTIF_PREFIX = "notification de changement de prénoms";

    public void generate(FormData data, File file) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            addHeading(document, data);
            addRequestHeader(document, data);
            addApplicantSection(document, data);
            addConsentSection(document, data);
            addFactsSection(document, data);
            addCompetenceSection(document, data);
            addLawSection(document, data);
            addEnFaitSection(document, data);
            addConclusionSection(document, data);
            addAttachments(document, data);
            try (FileOutputStream out = new FileOutputStream(file)) {
                document.write(out);
            }
        }
    }

    private void addHeading(XWPFDocument doc, FormData data) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setText("À Mesdames et Messieurs les Présidents");
        r.addBreak();
        r.setText("et Juges de la Chambre du Conseil");
        r.addBreak();
        String[] lines = data.getTribunal().split("\\R", -1);
        for (String line : lines) {
            if (!line.isEmpty()) {
                r.setText(line);
                r.addBreak();
            }
        }
        r.addBreak();
    }

    private void addRequestHeader(XWPFDocument doc, FormData data) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setText(data.isChangeNames()
                ? "Requête de changement de sexe et de prénoms à l'état civil"
                : "Requête de changement de sexe à l'état civil");
        r.addBreak();
        XWPFRun r2 = doc.createParagraph().createRun();
        r2.setText("Devant la Chambre du Conseil");
        r2.addBreak();
        r2.setText("(Article 1055-5 à 1055-9 du Code de procédure civile)");
        r2.addBreak();
        r2.setText("(Art. 61-5 à 61-8 du Code civil)");
        r2.addBreak();
        r2.setText("Requête à l'intention de Madame ou Monsieur le Président du tribunal");
        r2.addBreak();
        r2.addBreak();
    }

    private void addApplicantSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("À LA DEMANDE DE");
        tRun.addBreak();

        String civilBlock = data.honorificCivil() + " " + data.getLastNameUpper() + " " + data.getCivilFirstNames();
        String targetBlock = data.honorificTarget() + " " + data.getLastNameUpper() + " " + (data.isChangeNames() ? data.getChosenFirstNames() : data.getCivilFirstNames());

        XWPFRun r = doc.createParagraph().createRun();
        r.setText(targetBlock + " (" + civilBlock + " pour l'état civil)");
        r.addBreak();

        String dobLong;
        try {
            dobLong = LocalDate.parse(data.getBirthDate(), DOB_IN_FMT).format(DOB_OUT_FMT);
        } catch (Exception ex) {
            dobLong = data.getBirthDate();
        }

        XWPFRun r2 = doc.createParagraph().createRun();
        r2.setText(capFirst(data.adjBorn()) + " le " + dobLong + " à " + data.getBirthPlace() + ",");
        r2.addBreak();

        if (!blank(data.getNationality())) {
            XWPFRun rNat = doc.createParagraph().createRun();
            rNat.setText("De nationalité " + data.getNationality() + ",");
            rNat.addBreak();
        }

        XWPFRun rAddr = doc.createParagraph().createRun();
        rAddr.setText("Demeurant au " + data.getAddress() + ",");
        rAddr.addBreak();

        if (!blank(data.getProfession())) {
            XWPFRun rProf = doc.createParagraph().createRun();
            rProf.setText(data.getProfession() + ",");
            rProf.addBreak();
        }

        String civilLine = buildCivilLine(data.getMaritalStatus(), data.getChildrenInfo(), data.isPacsContracted());
        if (!blank(civilLine)) {
            XWPFRun rCivil = doc.createParagraph().createRun();
            rCivil.setText(civilLine);
            rCivil.addBreak();
        }

        XWPFRun r4 = doc.createParagraph().createRun();
        r4.setText("Faisant état de son consentement libre et éclairé.");
        r4.addBreak();
        r4.addBreak();
    }

    private String buildCivilLine(String marital, String children, boolean pacs) {
        StringBuilder sb = new StringBuilder();
        if (!blank(marital)) sb.append(marital.trim());
        if (!blank(children)) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(children.trim());
        }
        if (sb.length() > 0) sb.append(", ");
        sb.append(pacs ? "ayant contracté un Pacte Civil de Solidarité," : "n'ayant pas contracté de Pacte Civil de Solidarité,");
        return sb.toString();
    }

    private void addConsentSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("CONSENTEMENT LIBRE ET ÉCLAIRÉ");
        tRun.addBreak();

        String civilPart = data.getCivilFirstNames() + " " + data.getLastNameUpper();
        String chosenPart = (data.isChangeNames() ? data.getChosenFirstNames() : data.getCivilFirstNames()) + " " + data.getLastNameUpper();

        XWPFRun r = doc.createParagraph().createRun();
        String consent = "Je " + data.sousSigne() + " " + chosenPart + " (" + civilPart + " pour l'état civil), " + data.adjBorn() + " le "
                + formatDate(data.getBirthDate()) + " à " + data.getBirthPlace() + " demeurant au " + data.getAddress() + ", fais état de mon consentement libre et éclairé à la "
                + (data.isChangeNames() ? "modification de mes prénoms et de la mention relative à mon sexe" : "modification de la mention relative à mon sexe")
                + " dans les actes de mon état civil.";
        r.setText(consent);
        r.addBreak();
        r.setText("(À recopier manuscrit ci-dessous par la déclarante ou le déclarant, puis signer)");
        r.addBreak();
        r.addBreak();
        for (int i = 0; i < 4; i++) {
            XWPFRun line = doc.createParagraph().createRun();
            line.setText("____________________________________________________________________________________");
        }
        XWPFRun sig = doc.createParagraph().createRun();
        sig.setText("Signature");
        sig.addBreak();
        sig.addBreak();
    }

    private void addFactsSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("A L'HONNEUR DE VOUS EXPOSER QUE");
        tRun.addBreak();

        XWPFRun factsTitle = doc.createParagraph().createRun();
        factsTitle.setBold(true);
        factsTitle.setText("Les faits");
        factsTitle.addBreak();

        XWPFRun r = doc.createParagraph().createRun();
        String civilPart = data.getCivilFirstNames() + " " + data.getLastNameUpper();
        String chosenPart = (data.isChangeNames() ? data.getChosenFirstNames() : data.getCivilFirstNames()) + " " + data.getLastNameUpper();
        r.setText("« " + chosenPart + " » " + data.adjInscrit() + " à sa naissance sur les registres de l'état civil sous les prénoms « " + data.getCivilFirstNames() + " » et comme étant de sexe " + data.getCivilSex().toLowerCase(Locale.ROOT) + ", demande une modification de la mention de sexe" + (data.isChangeNames() ? " et de prénoms" : "") + " à l'état civil.");
        r.addBreak();

        String age = data.getAge();
        if (!age.isEmpty()) {
            XWPFRun rAge = doc.createParagraph().createRun();
            rAge.setText(capFirst(data.adjAge()) + " aujourd'hui de " + age + " ans, " + data.reqNoun() + " a annoncé son identité de genre à ses proches et vit socialement en tant que " + data.targetGenderNoun() + ".");
            rAge.addBreak();
        }

        if (!blank(data.getNarrative())) {
            XWPFRun rN = doc.createParagraph().createRun();
            rN.setText(data.getNarrative());
            rN.addBreak();
        }
        XWPFRun br = doc.createParagraph().createRun();
        br.addBreak();
    }

    private void addCompetenceSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("Sur la compétence du tribunal");
        tRun.addBreak();

        XWPFRun r = doc.createParagraph().createRun();
        r.setText("Vu l'article 1055-5 du Code de procédure civile : « La demande en modification de la mention du sexe et, le cas échéant, des prénoms, dans les actes de l'état civil, est portée devant le tribunal dans le ressort duquel soit la personne intéressée demeure, soit son acte de naissance a été dressé ou transcrit. »");
        r.addBreak();
        XWPFRun r2 = doc.createParagraph().createRun();
        r2.setText("Le domicile " + (data.isTargetFeminine() ? "de la requérante" : "du requérant") + " demeure à " + data.getAddress() + ". Le tribunal indiqué est donc compétent pour entendre la présente affaire.");
        r2.addBreak();
        XWPFRun r3 = doc.createParagraph().createRun();
        r3.setText("Par ailleurs, vu l'article 1055-7 du Code de la procédure civile, la représentation " + (data.isTargetFeminine() ? "de la requérante" : "du requérant") + " par un avocat n'est pas obligatoire : « La demande est formée par requête remise ou adressée au greffe. Le cas échéant, la requête précise si la demande tend également à un changement de prénoms. Le ministère d'avocat n'est pas obligatoire. »");
        r3.addBreak();
        r3.addBreak();
    }

    private void addLawSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("Sur la demande de rectification de la mention de sexe à l'état civil");
        tRun.addBreak();

        XWPFRun p = doc.createParagraph().createRun();
        p.setText("A) En droit :");
        p.addBreak();

        addLawParagraph(doc, "L'article 56 de la loi n° 2016-1547 du 18 novembre 2016 de modernisation de la Justice du XXIème siècle - validé par le Conseil Constitutionnel dans sa décision n° 2016-739 DC du 17 novembre 2016 - vient introduire quatre nouveaux articles dans le Code Civil quant au changement d'état civil pour les personnelles transsexuelles.");
        addLawParagraph(doc, "L'article 61-5 du Code Civil pose le principe que :");
        addLawParagraph(doc, "« Toute personne majeure ou mineur émancipée qui démontre par une réunion suffisante de faits que la mention relative à son sexe dans les actes de l'état civil ne correspondant pas à celui dans lequel elle se présente et dans lequel elle est connue peut en obtenir la modification.\nLes principaux de ces faits, dont la preuve peut être rapportée par tous moyens, peuvent être :\n1° Qu'elle se présente publiquement comme appartenant au sexe revendiqué ;\n2° Qu'elle est connue sous le sexe revendiqué de son entourage familial, amical ou professionnel ;\n3° Qu'elle a obtenu le changement de son prénom afin qu'il corresponde au sexe revendiqué ; »");
        addLawParagraph(doc, "L'article 61-6 dudit code ajoute :");
        addLawParagraph(doc, "« La demande est présentée devant le tribunal de grande instance. Le demandeur fait état de son consentement libre et éclairé à la modification de la mention relative à son sexe dans les actes de l'état civil et produit tous éléments de preuve au soutien de sa demande. Le fait de ne pas avoir subi des traitements médicaux, une opération chirurgicale ou une stérilisation ne peut motiver le refus de faire droit à la demande.\nLe tribunal constate que le demandeur satisfait aux conditions fixées à l'article 61-5 et ordonne la modification de la mention relative au sexe ainsi que, le cas échéant, des prénoms, dans les actes de l'état civil. »");
        addLawParagraph(doc, "Une fois le changement d'état civil accordé l'article 61-7 du Code Civil précise que :");
        addLawParagraph(doc, "« Mention de la décision de modification du sexe et, le cas échéant, des prénoms est portée en marge de l'acte de naissance de l'intéressé, à la requête du procureur de la République, dans les quinze jours suivant la date à laquelle cette décision est passée en force de chose jugée.\nPar dérogation à l'article 61-4, les modifications de prénoms corrélatives à une décision de modification de sexe ne sont portées en marge des actes de l'état civil des conjoints et enfants qu'avec le consentement des intéressés ou de leurs représentants légaux. Les articles 100 et 101 sont applicables aux modifications de sexe. »");
        addLawParagraph(doc, "Enfin, l'article 61-8 du Code civil dispose que :");
        addLawParagraph(doc, "« La modification de la mention du sexe dans les actes de l'état civil est sans effet sur les obligations contractées à l'égard de tiers ni sur les filiations établies avant cette modification. »");
        addLawParagraph(doc, "Ce faisant le changement de sexe à l'état civil est totalement démédicalisé et se fonde désormais uniquement sur la détermination sociale de son sexe par la personne et sa reconnaissance par son entourage comme le précise le circulaire du 10 mai 2017 de présentation des dispositions de l'article 56 de la loi n° 2016-1547 du 18 novembre 2016 de modernisation de la justice du XXIe siècle concernant les procédures judiciaires de changement de prénom et de modification de la mention du sexe à l'état civil, NOR : JUSC1709389C :");
        addLawParagraph(doc, "« L'article 56 crée par ailleurs une procédure de modification de la mention du sexe à l'état civil, simplifiée et démédicalisée sous le contrôle du juge. »");
        addLawParagraph(doc, "Le législateur a en outre pris la peine d'indiquer directement dans la loi que « Le fait de ne pas avoir subi des traitements médicaux, une opération chirurgicale ou une stérilisation ne peut motiver le refus de faire droit à la demande. » (article 61-6 du Code Civil).");
        addLawParagraph(doc, "Cela a été confirmé par la cour d'appel de Montpellier dans l'arrêt du 15 mars 2017 :");
        addLawParagraph(doc, "« La personne ne doit plus établir [...] la réalité du syndrome transsexuel [...] ainsi que le caractère irréversible de la transformation de l'apparence. La reconnaissance sociale, posée par la loi nouvelle du 18 novembre 2016 comme seule condition à la modification de la mention du sexe à l'état civil. »");
        addLawParagraph(doc, "La France a également été condamnée par la Cour Européenne des Droits de l'Homme le 6 avril 2017 :");
        addLawParagraph(doc, "« Le rejet de la demande [...] tendant à la modification de leur état civil au motif qu'ils n'avaient pas établi le caractère irréversible de la transformation de leur apparence, c'est-à-dire démontré avoir subi une opération stérilisante ou un traitement médical entrainant une très forte probabilité de stérilité, s'analyse en un manquement par l'Etat défendeur à son obligation positive de garantir le droit de ces derniers au respect de leur vie privée. Il y a donc, de ce chef, violation de l'article 8 de la Convention à leur égard. »");
        addLawParagraph(doc, "Le caractère facultatif des preuves médicales a d'ailleurs été rappelé par le Défenseur des droits dans sa décision n°2018-122 du 12 avril 2018 :");
        addLawParagraph(doc, "« Décide de prendre acte du dispositif mis en place par le tribunal de grande instance de A. modifiant la notice de pièces jointe aux dossiers de demande de modification de la mention relative au sexe à l’état civil et rendant facultatives les pièces médicales.\nDécide de recommander au ministre de Justice de veiller à ce que les demandeurs soient informés du caractère facultatif de la communication de pièces médicales à leur dossier, et que des instructions soient adressées dans ce sens. »");
        addLawParagraph(doc, "De ce fait, les conditions au changement de la mention de sexe à l'état civil disposées par l'article 61-5 du Code Civil sont les seules à devoir être satisfaites.");
        XWPFRun br = doc.createParagraph().createRun();
        br.addBreak();
    }

    private void addEnFaitSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("EN FAIT");
        tRun.addBreak();

        String honor = data.honorificTarget();
        String firsts = data.isChangeNames() ? data.getChosenFirstNames() : data.getCivilFirstNames();
        String full = honor + " " + firsts + " " + data.getLastNameUpper();
        String sexeTarget = data.targetGenderAdj();
        String sexeCivil = data.civilGenderAdj();

        XWPFRun r1 = doc.createParagraph().createRun();
        r1.setText("Dans les faits, il est établi que " + full + " se présente publiquement comme appartenant au sexe " + sexeTarget + " et " + data.pronounQuIlElle() + " est " + data.adjConnuConnue() + " sous cette identité " + (data.isTargetFeminine() ? "féminine" : "masculine") + " par sa famille, son entourage amical, professionnel ou académique ainsi que dans toutes les interactions sociales " + data.pronounQuIlElle() + " entreprend quotidiennement.");
        r1.addBreak();

        XWPFRun r2 = doc.createParagraph().createRun();
        r2.setText("Par conséquent, le Tribunal judiciaire ne pourra manquer d’ordonner la suppression de la mention « sexe " + sexeCivil + " » pour la remplacer par la mention « sexe " + sexeTarget + " » sur son acte de naissance.");
        r2.addBreak();
        r2.addBreak();
    }

    private void addConclusionSection(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("EN CONSÉQUENCE DE QUOI");
        tRun.addBreak();

        XWPFRun r = doc.createParagraph().createRun();
        r.setText("Sur la compétence juridictionnelle : Vu les articles 1055-5 à 1055-9 du Code de procédure civile ;");
        r.addBreak();
        XWPFRun r0 = doc.createParagraph().createRun();
        r0.setText("Sur le fond : Vu les articles 9, 61-5 et suivants du Code civil ; Vu l'article 8 de la Convention européenne des droits de l'homme ;");
        r0.addBreak();
        r0.addBreak();

        XWPFRun r1 = doc.createParagraph().createRun();
        r1.setText("Par ces motifs, " + data.reqNoun() + " requiert qu'il plaise au tribunal de :");
        r1.addBreak();

        XWPFRun bullet1 = doc.createParagraph().createRun();
        bullet1.setText("– Ordonner que l'acte de naissance " + (data.isTargetFeminine() ? "de la requérante" : "du requérant") + ", dressé à " + data.getBirthPlace() + ", soit rectifié en ce sens que la mention « sexe " + data.getCivilSex().toLowerCase(Locale.ROOT) + " » soit remplacée par la mention « sexe " + data.getTargetSex().toLowerCase(Locale.ROOT) + " »" + (data.isChangeNames() ? " et que les prénoms d'origine soient remplacés par « " + data.getChosenFirstNames() + " »" : "") + ";");

        XWPFRun bullet2 = doc.createParagraph().createRun();
        if (data.isChangeNames()) {
            bullet2.setText("– Rappeler qu'en vertu de l'article 61-7 du Code civil, la mention de la décision de la modification du sexe et, le cas échéant, des prénoms est portée en marge de l'acte de naissance de l'intéressé dans les quinze jours suivant la date à laquelle cette décision est passée en force de chose jugée ;");
        } else {
            bullet2.setText("– Rappeler qu'en vertu de l'article 61-7 du Code civil, la mention de la décision de la modification du sexe est portée en marge de l'acte de naissance de l'intéressé dans les quinze jours suivant la date à laquelle cette décision est passée en force de chose jugée ;");
        }

        XWPFRun bullet3 = doc.createParagraph().createRun();
        bullet3.setText("– Ordonner qu'aucune expédition des actes d'état civil dans la mention desdites rectifications ne soit délivrée.");

        XWPFRun p2 = doc.createParagraph().createRun();
        p2.addBreak();
        p2.setText(data.reqNounCapitalized() + " procédera aux démarches tendant à la reconnaissance de la décision du changement de la mention du sexe" + (data.isChangeNames() ? " ainsi que des prénoms" : "") + " à l’état civil auprès des autorités locales compétentes, dès que celle-ci aura été prise.");
        p2.addBreak();
        p2.setText(data.reqNounCapitalized() + " atteste sur l’honneur qu’aucune procédure de changement de la mention du sexe" + (data.isChangeNames() ? " et des prénoms" : "") + " à l’état civil n’est actuellement en cours devant les juridictions françaises et qu’aucune demande de la sorte n’est actuellement examinée par un juge aux affaires familiales.");
        p2.addBreak();
        p2.addBreak();
        p2.setText("Fait à " + data.getCurrentCity() + ", le " + LocalDate.now().format(TODAY_FMT));
        p2.addBreak();
        p2.addBreak();
        p2.setText("Signature :");
        p2.addBreak();
        p2.addBreak();
    }

    private void addAttachments(XWPFDocument doc, FormData data) {
        XWPFParagraph title = doc.createParagraph();
        XWPFRun tRun = title.createRun();
        tRun.setBold(true);
        tRun.setText("Bordereau des pièces jointes à la requête");
        tRun.addBreak();
        int i = 1;
        for (String item : data.getAttachments()) {
            String low = item == null ? "" : item.toLowerCase(Locale.ROOT);
            if (data.isChangeNames() && low.startsWith(NOTIF_PREFIX)) continue;
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();
            r.setText("N°" + (i++) + " – " + item);
        }
    }

    private static String capFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        if (s.length() == 1) return s.toUpperCase(Locale.ROOT);
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String formatDate(String ddmmyyyy) {
        try {
            return LocalDate.parse(ddmmyyyy, DOB_IN_FMT).format(DOB_OUT_FMT);
        } catch (Exception e) {
            return ddmmyyyy;
        }
    }

    private static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void addLawParagraph(XWPFDocument doc, String text) {
        for (String part : text.split("\\R")) {
            XWPFRun r = doc.createParagraph().createRun();
            r.setText(part);
            r.addBreak();
        }
    }
}