package com.example.oscar.radio;


final class CoverPhoto {
    public static int get(String musor, boolean forCard) {

        switch(musor) {
            case "A bibliafordítás reneszánsza":
                return R.drawable.new_bibliaforditas_reneszansza;
            case "Ami fontos! - Családi magazin":
                return R.drawable.new_ami_fontos;
            case "Arcél":
                return R.drawable.new_arcel;
            case "Archívum helyett":
                return R.drawable.new_archivum_helyett;
            case "Atmoszféra":
                return R.drawable.new_atmoszfera;
            case "Best of Hit hits":
                return R.drawable.new_best_of_hit_hits;
            case "Best Of Hit Live":
                return R.drawable.new_best_of_hit_live;
            case "Best Of Középpont":
                return R.drawable.new_best_of_kozeppont;
            case "Billboard":
                return R.drawable.new_bllboard;
            case "Fordulat - csodák, bizonyságok":
                return R.drawable.new_fordulat;
            case "Full kontakt":
                return R.drawable.new_fullkontakt;
            case "Gospel":
                return R.drawable.new_gospel;
            case "Háttér":
                return R.drawable.new_hatter;
            case "Hétköznapi Hősök":
                return R.drawable.new_hetkoznapi_hosok;
            case "Hit hattól":
                return R.drawable.new_hit_hattol;
            case "Hit Live":
                return R.drawable.new_hitlive;
            case "Igaz mesék gyerekeknek!":
                return R.drawable.new_mese;
            case "Igeidő – a pásztor órája":
                return R.drawable.new_igeido;
            case "Istentisztelet-közvetítés":
                return R.drawable.new_it;
            case "Középpont":
                return R.drawable.new_kozeppont;
            case "Közvilágítás":
                return R.drawable.new_kozvilagitas;
            case "Kultivitamin":
                return R.drawable.new_kultivitamin;
            case "Látószög":
                return R.drawable.new_latoszog;
            case "Legjava - Válogatás":
                return R.drawable.new_legjava;
            case "Lélekmód":
                return R.drawable.new_lelekmod;
            case "Múltidő":
                return R.drawable.new_multido;
            case "Női szemmel":
                return R.drawable.new_noi_szemmel;
            case "Olvassuk együtt egy év alatt a Bibliát!":
                return R.drawable.cover_bibliaolvasas2; // TODO
            case "Prevenció":
                return R.drawable.new_prevencio;
            case "Romaút":
                return R.drawable.new_romaut;
            case "Szabad Szombat":
                return R.drawable.new_szabad_szombat;
            case "Szélcsatorna":
                return R.drawable.new_szelcsatorna;
            case "Vidám Vasárnap":
                return R.drawable.new_vidam_vasarnap;
            default:
                if(!forCard)
                    return R.drawable.cover_no22;
                else
                    return R.drawable.cover_hitradio_cropped2;


        }
    }


}
