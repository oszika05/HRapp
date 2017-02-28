package com.example.oscar.radio;


final class CoverPhoto {
    public static int get(String musor, boolean forCard) {

        switch(musor) {
            case "Középpont":
                return R.drawable.cover_kozeppont2;
            case "Hit hattól":
                return R.drawable.cover_hithattol2;
            case "Olvassuk együtt egy év alatt a Bibliát!":
                return R.drawable.cover_bibliaolvasas2;
            case "Hit Live":
                return R.drawable.cover_hitlive2;
            case "Full kontakt":
                return R.drawable.cover_fullkontakt2;
            case "Ami fontos! - Családi magazin":
                return R.drawable.cover_amifontos2;
            case "Pipere - szépségmagazin":
                return R.drawable.cover_pipere2;
            default:
                if(!forCard)
                    return R.drawable.cover_no22;
                else
                    return R.drawable.cover_hitradio_cropped2;


        }
    }


}
