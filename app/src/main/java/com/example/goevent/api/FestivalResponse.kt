package com.example.goevent.api

data class FestivalResponse(
    val results:  List<Festival>
)
data class Festival(
    val nom_du_festival: String?,
    val site_internet_du_festival: String?,
    val discipline_dominante: String?,
    val sous_categorie_musique: String?,
    val sous_categorie_musique_cnm: String?,
    val periode_principale_de_deroulement_du_festival: String?,
    val adresse_e_mail: String?,
    val adresse_postale: String?,
    val identifiant: String?,
    val geocodage_xy: GeoLocation?,
    val region_principale_de_deroulement: String?,
    val departement_principal_de_deroulement: String?,
    val commune_principale_de_deroulement: String?,
    val code_postal_de_la_commune_principale_de_deroulement: String?,
    var distance: Double?,
    var adresse: String?

)
data class GeoLocation(
    val lat: Double?,
    val lon: Double?
)
data class Adresse(
    val numero_de_voie: String?,
    val type_de_voie_rue_avenue_boulevard_etc: String?,
    val nom_de_la_voie: String?,
    val adresse_postale: String?,
    val complement_d_adresse_facultatif: String?,
    val code_postal: String?,

)
