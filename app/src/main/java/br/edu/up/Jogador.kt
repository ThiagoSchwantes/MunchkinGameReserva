package br.edu.up

data class Jogador(
    var id: Int,
    var nome: String,
    var level: Int = 0,
    var bonusEquipamento: Int = 0,
    var modificadores: Int = 0
) {
    val poderTotal get() = level + bonusEquipamento + modificadores
}