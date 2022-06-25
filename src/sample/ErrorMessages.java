package sample;

public interface ErrorMessages {
    //interface sa svim mogucim porukama o greskama (mozda dodavati i 'pozitivne' poruke samo preimenuj interface)

    String inputError = "Unesite username/password!";
    String errorUserPass = "Pogresno ste unijeli username/password!";
    String errorUsernameExist = "Username pod tim imenom vec postoji!";
    String errorSelected = "Izaberite direktorijum da kreirate tekstualni fajl";
    String errorOpen = "Greska pri otvaranju forme. Pokusajte ponovo.";
    String errorSelectedDownload = "Ne mozete da skinete direktorijum, mora biti neki fajl";
    String errorDownload = "Greska pri downloadu";
    String successDownload = "Uspjesno ste skinuli fajl";
    String successUpload = "Uspjesno se uploadovali fajl na fajl sistem";
    //String errorUpload = "Greska pri uploadu";
    String notSelectedFile = "Niste selektovali fajl";
    String onlyTextFile = "Niste selektovali tekstualni fajl.";
    String errorSelectedDelete = "Ne mozete obrisati direktorijum";
    String errorDelete = "Doslo je do greske pri brisanju fajla";
    String successDelete = "Uspjesno ste obrisali fajl";
    String errorChoose = "Izaberite file koji zelite da otvorite";
    String errorOpenFile = "Greska pri otvaranju fajla.";
    String errorLogout = "Greska pri odjavljivanju";
    String successCreated = "Uspjesno ste kreirali datoteku";
    String error = "Doslo je do greske. Pokusajte ponovo";
    String errorInputCreate = "Unesite ime/sadrzaj da bi kreirali datoteku";
    String successEdit = "Uspjesno ste editovali";
    String errorInputFile = "Doslo je do greske pri upisu u fajl";
    String successRegistration = "Uspjesno ste registrovani na SEF-System";
    String errorHomeDir = "Greska pri kreiranju home direktorijuma";
    String errorRegistration = "Doslo je do greske pri registrovanu. Pokusajte ponovo";
}