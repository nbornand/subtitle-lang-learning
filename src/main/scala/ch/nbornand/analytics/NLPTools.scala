package ch.nbornand.analytics

/**


CC Coordinating conjunction
CD Cardinal number
DT Determiner
EX Existential there
FW Foreign word
IN Preposition or subordinating conjunction
JJ Adjective
JJR Adjective, comparative
JJS Adjective, superlative
LS List item marker
MD Modal
NN Noun, singular or mass
NNS Noun, plural
NNP Proper noun, singular
NNPS Proper noun, plural
PDT Predeterminer
POS Possessive ending
PRP Personal pronoun
PRP$ Possessive pronoun
RB Adverb
RBR Adverb, comparative
RBS Adverb, superlative
RP Particle
SYM Symbol
TO to
UH Interjection
VB Verb, base form
VBD Verb, past tense
VBG Verb, gerund or present participle
VBN Verb, past participle
VBP Verb, non­3rd person singular present
VBZ Verb, 3rd person singular present
WDT Wh­determiner
WP Wh­pronoun interogative
WP$ Possessive wh­pronoun
WRB Wh­adverb
  */
class NLPTools {
  Map("Antwortpartikel" -> "Answer particles",
    "Konjunktionaladverb" -> "conjunctive adverb",
    "Demonstrativpronomen" -> "demonstrative pronoun",
    "Konjunktion" -> "conjunction",
    "Verb, Adjektiv, Substantiv" -> "Verb, adjective , noun",
    "Numerale" -> "numeral",
    "Konjugierte Form" -> "Conjugated form",
    "Gebundenes Lexem" -> "Hard lexeme",
    "Fokuspartikel" -> "focus particles",
    "Postposition" -> "postposition",
    "Nachname" -> "Surname",
    "Adverb" -> "adverb",
    "Possessivpronomen" -> List("PRP$"),
    "Zusammenbildung" -> "Together Education",
    "Interjektion" -> "interjection",
    "Suffix" -> "suffix",
    "Onomatopoetikum" -> "onomatopoeia",
    "Modaladverb" -> "Modaladverb",
    "Negationspartikel" -> "negative particle",
    "Verb" -> List("VB", "VBD", "VBG", "VBN", "VBP", "VBZ"),
    "Suffixoid" -> "Suffixoid",
    "Substantivierter Infinitiv" -> "Substantivierter infinitive",
    "Reflexivpronomen" -> "reflexive pronouns",
    "Eigenname" -> List("NNP", "NPPS"),
    "Geflügeltes Wort" -> "household word",
    "Präfix" -> "prefix",
    "Partizip II" -> "past participle",
    "Abkürzung" -> "abbreviation",
    "Pronominaladverb" -> "pronominal adverb",
    "Präposition" -> "preposition",
    "Interrogativpronomen" -> List("WP"),
    "Sprichwort" -> "proverb",
    "Modalpartikel" -> "modal particle",
    "Partizip I" -> "participle",
    "Lokaladverb" -> "locative adverb",
    "Hilfsverb" -> "auxiliary verb",
    "Merk spruch" -> "Merkspruch",
    "Substantiv" -> "noun",
    "Ortsnamengrundwort" -> "Place names Basic word",
    "Wortverbindung" -> "combination of words",
    "Relativpronomen" -> "relative pronoun",
    "Kontraktion" -> "contraction",
    "Gradpartikel" -> "degrees particles",
    "Partikel" -> "particle",
    "Redewendung" -> "Phrase",
    "Deklinierte Form" -> "declension",
    "Reziprokpronomen" -> "reciprocal pronoun",
    "Erweiterter Infinitiv" -> "extended infinitive",
    "Personalpronomen" -> "Personal pronouns",
    "Adjektiv" -> List("JJ","JJR","JJS"),
    "Pronomen" -> "pronoun",
    "Subjunktion" -> "subjunction",
    "Temporal adverb" -> "Temporal adverb",
    "Interrogativ adverb" -> "interrogative adverb",
    "Affix" -> "affix",
    "Artikel" -> "items",
    "Indefinit pronomen" -> "indefinite pronoun",
    "Toponym" -> "toponym",
    "Grussformel" -> "salutation",
    "Mehrwort benennung" -> "More word designation")
}

