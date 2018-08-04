package com.example.sebastianczuma.officevisor.Interfaces;

/**
 * Created by sebastianczuma on 23.08.2016.
 */
public interface UrlAddress {
    String urlBase = "http://195.22.33.4";

    String urlLoginManual = urlBase + "/badania/logowanie.php";
    String urlLoginAuto = urlBase + "/badania/autologowanie.php";
    String urlRegister = urlBase + "/badania/rejestracja.php";
    String urlCallLogUpload = urlBase + "/badania/pobieranie_danych_urzadzen.php";
    String urlGraphData = urlBase + "/badania/pobieranie_danych_caly_tydzien.php";
}
