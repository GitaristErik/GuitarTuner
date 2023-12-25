package com.example.guitartuner.domain.repository.tuner

import com.example.guitartuner.domain.entity.tuner.Note

enum class ChromaticScale(
    val note: Note,
    val octave: Int,
    val frequency: Float,
    val semitone: Boolean = false
) {

    C0(Note.C, 0, 16.35f),
    C0_SHARP(Note.C, 0, 17.32f, true),
    D0(Note.D, 0, 18.35f),
    D0_SHARP(Note.D, 0, 19.45f, true),
    E0(Note.E, 0, 20.60f),
    F0(Note.F, 0, 21.83f),
    F0_SHARP(Note.F, 0, 23.12f, true),
    G0(Note.G, 0, 24.50f),
    G0_SHARP(Note.G, 0, 25.96f, true),
    A0(Note.A, 0, 27.50f),
    A0_SHARP(Note.A, 0, 29.14f, true),
    B0(Note.B, 0, 30.87f),

    C1(Note.C, 1, 32.70f),
    C1_SHARP(Note.C, 1, 34.65f, true),
    D1(Note.D, 1, 36.71f),
    D1_SHARP(Note.D, 1, 38.89f, true),
    E1(Note.E, 1, 41.20f),
    F1(Note.F, 1, 43.65f),
    F1_SHARP(Note.F, 1, 46.25f, true),
    G1(Note.G, 1, 49.00f),
    G1_SHARP(Note.G, 1, 51.91f, true),
    A1(Note.A, 1, 55.00f),
    A1_SHARP(Note.A, 1, 58.27f, true),
    B1(Note.B, 1, 61.74f),

    C2(Note.C, 2, 65.41f),
    C2_SHARP(Note.C, 2, 69.30f, true),
    D2(Note.D, 2, 73.42f),
    D2_SHARP(Note.D, 2, 77.78f, true),
    E2(Note.E, 2, 82.41f),
    F2(Note.F, 2, 87.31f),
    F2_SHARP(Note.F, 2, 92.50f, true),
    G2(Note.G, 2, 98.00f),
    G2_SHARP(Note.G, 2, 103.83f, true),
    A2(Note.A, 2, 110.00f),
    A2_SHARP(Note.A, 2, 116.54f, true),
    B2(Note.B, 2, 123.47f),

    C3(Note.C, 3, 130.81f),
    C3_SHARP(Note.C, 3, 138.59f, true),
    D3(Note.D, 3, 146.83f),
    D3_SHARP(Note.D, 3, 155.56f, true),
    E3(Note.E, 3, 164.81f),
    F3(Note.F, 3, 174.61f),
    F3_SHARP(Note.F, 3, 185.00f, true),
    G3(Note.G, 3, 196.00f),
    G3_SHARP(Note.G, 3, 207.65f, true),
    A3(Note.A, 3, 220.00f),
    A3_SHARP(Note.A, 3, 233.08f, true),
    B3(Note.B, 3, 246.94f),

    C4(Note.C, 4, 261.63f),
    C4_SHARP(Note.C, 4, 277.18f, true),
    D4(Note.D, 4, 293.66f),
    D4_SHARP(Note.D, 4, 311.13f, true),
    E4(Note.E, 4, 329.63f),
    F4(Note.F, 4, 349.23f),
    F4_SHARP(Note.F, 4, 369.99f, true),
    G4(Note.G, 4, 392.00f),
    G4_SHARP(Note.G, 4, 415.30f, true),
    A4(Note.A, 4, 440.00f),
    A4_SHARP(Note.A, 4, 466.16f, true),
    B4(Note.B, 4, 493.88f),

    C5(Note.C, 5, 523.25f),
    C5_SHARP(Note.C, 5, 554.37f, true),
    D5(Note.D, 5, 587.33f),
    D5_SHARP(Note.D, 5, 622.25f, true),
    E5(Note.E, 5, 659.25f),
    F5(Note.F, 5, 698.46f),
    F5_SHARP(Note.F, 5, 739.99f, true),
    G5(Note.G, 5, 783.99f),
    G5_SHARP(Note.G, 5, 830.61f, true),
    A5(Note.A, 5, 880.00f),
    A5_SHARP(Note.A, 5, 932.33f, true),
    B5(Note.B, 5, 987.77f),

    C6(Note.C, 6, 1046.50f),
    C6_SHARP(Note.C, 6, 1108.73f, true),
    D6(Note.D, 6, 1174.66f),
    D6_SHARP(Note.D, 6, 1244.51f, true),
    E6(Note.E, 6, 1318.51f),
    F6(Note.F, 6, 1396.91f),
    F6_SHARP(Note.F, 6, 1479.98f, true),
    G6(Note.G, 6, 1567.98f),
    G6_SHARP(Note.G, 6, 1661.22f, true),
    A6(Note.A, 6, 1760.00f),
    A6_SHARP(Note.A, 6, 1864.66f, true),
    B6(Note.B, 6, 1975.53f),

    C7(Note.C, 7, 2093.00f),
    C7_SHARP(Note.C, 7, 2217.46f, true),
    D7(Note.D, 7, 2349.32f),
    D7_SHARP(Note.D, 7, 2489.02f, true),
    E7(Note.E, 7, 2637.02f),
    F7(Note.F, 7, 2793.83f),
    F7_SHARP(Note.F, 7, 2959.96f, true),
    G7(Note.G, 7, 3135.96f),
    G7_SHARP(Note.G, 7, 3322.44f, true),
    A7(Note.A, 7, 3520.00f),
    A7_SHARP(Note.A, 7, 3729.31f, true),
    B7(Note.B, 7, 3951.07f),

    C8(Note.C, 8, 4186.01f),
    C8_SHARP(Note.C, 8, 4434.92f, true),
    D8(Note.D, 8, 4698.63f),
    D8_SHARP(Note.D, 8, 4978.03f, true),
    E8(Note.E, 8, 5274.04f),
    F8(Note.F, 8, 5587.65f),
    F8_SHARP(Note.F, 8, 5919.91f, true),
    G8(Note.G, 8, 6271.93f),
    G8_SHARP(Note.G, 8, 6644.88f, true),
    A8(Note.A, 8, 7040.00f),
    A8_SHARP(Note.A, 8, 7458.62f, true),
    B8(Note.B, 8, 7902.13f);

    val formattedFrequency by lazy { FREQUENCY_FORMAT.format(frequency) }

    companion object {
        const val FREQUENCY_FORMAT = "%.2f"

        val notes by lazy {
            entries.sortedBy { it.frequency }
        }

        fun getFlatNote(note: Note) = when (note) {
            Note.C -> Note.D
            Note.D -> Note.E
            Note.F -> Note.G
            Note.G -> Note.A
            Note.A -> Note.B
            else -> throw IllegalArgumentException("Can't convert $note to flat")
        }
    }
}

/*

 85 = {Pair@24007} (C#6, 2217.4610478149766)
 65 = {Pair@23987} (F4, 698.4564628660078)
 78 = {Pair@24000} (F#5, 1479.9776908465376)
 56 = {Pair@23978} (G#3, 415.3046975799451)
 25 = {Pair@23947} (C#1, 69.29565774421802)
 26 = {Pair@23948} (D1, 73.41619197935188)
 89 = {Pair@24011} (F6, 2793.825851464031)
 10 = {Pair@23932} (A#-1, 29.13523509488062)
 63 = {Pair@23985} (D#4, 622.2539674441618)
 93 = {Pair@24015} (A6, 3520.0)
 29 = {Pair@23951} (F1, 87.30705785825097)
 96 = {Pair@24018} (C7, 4186.009044809578)
 40 = {Pair@23962} (E2, 164.81377845643496)
 83 = {Pair@24005} (B5, 1975.533205024496)
 39 = {Pair@23961} (D#2, 155.56349186104046)
 73 = {Pair@23995} (C#5, 1108.7305239074883)
 20 = {Pair@23942} (G#0, 51.91308719749314)
 53 = {Pair@23975} (F3, 349.2282314330039)
 70 = {Pair@23992} (A#4, 932.3275230361799)
 34 = {Pair@23956} (A#1, 116.54094037952248)
 80 = {Pair@24002} (G#5, 1661.2187903197805)
 87 = {Pair@24009} (D#6, 2489.0158697766474)
 31 = {Pair@23953} (G1, 97.99885899543733)
 3 = {Pair@23925} (D#-1, 19.445436482630058)
 23 = {Pair@23945} (B0, 61.7354126570155)
 46 = {Pair@23968} (A#2, 233.08188075904496)
 66 = {Pair@23988} (F#4, 739.9888454232688)
 60 = {Pair@23982} (C4, 523.2511306011972)
 21 = {Pair@23943} (A0, 55.0)
 67 = {Pair@23989} (G4, 783.9908719634985)
 47 = {Pair@23969} (B2, 246.94165062806206)
 71 = {Pair@23993} (B4, 987.7666025122483)
 98 = {Pair@24020} (D7, 4698.63628667852)
 62 = {Pair@23984} (D4, 587.3295358348151)
 48 = {Pair@23970} (C3, 261.6255653005986)
 8 = {Pair@23930} (G#-1, 25.956543598746574)
 24 = {Pair@23946} (C1, 65.40639132514966)
 51 = {Pair@23973} (D#3, 311.1269837220809)
 33 = {Pair@23955} (A1, 110.0)
 1 = {Pair@23923} (C#-1, 17.323914436054505)
 69 = {Pair@23991} (A4, 880.0)
 84 = {Pair@24006} (C6, 2093.004522404789)
 28 = {Pair@23950} (E1, 82.4068892282175)
 79 = {Pair@24001} (G5, 1567.981743926997)
 74 = {Pair@23996} (D5, 1174.6590716696303)
 16 = {Pair@23938} (E0, 41.20344461410875)
 36 = {Pair@23958} (C2, 130.8127826502993)
 76 = {Pair@23998} (E5, 1318.5102276514797)
 14 = {Pair@23936} (D0, 36.70809598967594)
 90 = {Pair@24012} (F#6, 2959.955381693075)
 52 = {Pair@23974} (E3, 329.6275569128699)
 5 = {Pair@23927} (F-1, 21.826764464562746)
 54 = {Pair@23976} (F#3, 369.9944227116344)
 77 = {Pair@23999} (F5, 1396.9129257320155)
 86 = {Pair@24008} (D6, 2349.31814333926)
 42 = {Pair@23964} (F#2, 184.9972113558172)
 32 = {Pair@23954} (G#1, 103.82617439498628)
 2 = {Pair@23924} (D-1, 18.354047994837977)
 94 = {Pair@24016} (A#6, 3729.3100921447194)
a = {ArrayList@23803}  size = 120
 41 = {Pair@23963} (F2, 174.61411571650194)
 55 = {Pair@23977} (G3, 391.99543598174927)
 61 = {Pair@23983} (C#4, 554.3652619537442)
 27 = {Pair@23949} (D#1, 77.78174593052023)
 44 = {Pair@23966} (G#2, 207.65234878997256)
 59 = {Pair@23981} (B3, 493.8833012561241)
 64 = {Pair@23986} (E4, 659.2551138257398)
 9 = {Pair@23931} (A-1, 27.5)
 75 = {Pair@23997} (D#5, 1244.5079348883237)
 88 = {Pair@24010} (E6, 2637.02045530296)
 57 = {Pair@23979} (A3, 440.0)
 81 = {Pair@24003} (A5, 1760.0)
 99 = {Pair@24021} (D#7, 4978.031739553295)
 68 = {Pair@23990} (G#4, 830.6093951598903)
 17 = {Pair@23939} (F0, 43.653528929125486)
 30 = {Pair@23952} (F#1, 92.4986056779086)
 49 = {Pair@23971} (C#3, 277.1826309768721)
 58 = {Pair@23980} (A#3, 466.1637615180899)
 18 = {Pair@23940} (F#0, 46.2493028389543)
 4 = {Pair@23926} (E-1, 20.601722307054366)
 97 = {Pair@24019} (C#7, 4434.922095629953)
 11 = {Pair@23933} (B-1, 30.86770632850775)
 0 = {Pair@23922} (C-1, 16.351597831287414)
 72 = {Pair@23994} (C5, 1046.5022612023945)
 37 = {Pair@23959} (C#2, 138.59131548843604)
 15 = {Pair@23937} (D#0, 38.890872965260115)
 35 = {Pair@23957} (B1, 123.47082531403103)
 43 = {Pair@23965} (G2, 195.99771799087463)
 92 = {Pair@24014} (G#6, 3322.437580639561)
 95 = {Pair@24017} (B6, 3951.066410048992)
 50 = {Pair@23972} (D3, 293.6647679174076)
 45 = {Pair@23967} (A2, 220.0)
 12 = {Pair@23934} (C0, 32.70319566257483)
 13 = {Pair@23935} (C#0, 34.64782887210901)
 22 = {Pair@23944} (A#0, 58.27047018976124)
 6 = {Pair@23928} (F#-1, 23.12465141947715)
 38 = {Pair@23960} (D2, 146.8323839587038)
 7 = {Pair@23929} (G-1, 24.499714748859326)
 82 = {Pair@24004} (A#5, 1864.6550460723597)
 19 = {Pair@23941} (G0, 48.999429497718666)
 91 = {Pair@24013} (G6, 3135.9634878539946)

 */
