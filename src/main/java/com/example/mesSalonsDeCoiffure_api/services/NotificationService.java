package com.example.mesSalonsDeCoiffure_api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    // Remplacement de System.out par un Logger
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // USER STORY : Rappel WhatsApp 24h avant
    // Cette fonction s'exécute automatiquement TOUTES LES HEURES (cron = "0 0 * * * *")
    @Scheduled(cron = "0 0 * * * *")
    public void envoyerRappelsWhatsapp() {
        logger.info("🔍 Vérification des RDV dans 24h...");
        
        // 1. Chercher dans la base les Réservations prévues pour (Maintenant + 24 heures)
        // 2. Pour chaque RDV, vérifier si le client a `notifsWhatsapp == true`
        // 3. Si OUI -> Appeler l'API de Twilio (ou Meta) pour envoyer un SMS/WhatsApp au numéro du client !
        
        // Exemple (Pseudo-code) :
        // if(client.isNotifsWhatsapp()) { Twilio.sendMessage(client.getTelephone(), "Votre rdv est demain !"); }
    }

    // USER STORY : Rappels réguliers pour revenir au salon
    // S'exécute tous les jours à 10h00 du matin
    @Scheduled(cron = "0 0 10 * * *")
    public void envoyerRappelsEntretien() {
        logger.info("🔍 Vérification des clients absents depuis 1 mois...");
        
        // 1. Chercher les clients avec `rappelsReguliers == true`
        // 2. Regarder la date de leur dernière réservation (Si > 30 jours)
        // 3. Envoyer un Email automatique : "Jean, votre coiffure a besoin d'un rafraîchissement !"
    }
}