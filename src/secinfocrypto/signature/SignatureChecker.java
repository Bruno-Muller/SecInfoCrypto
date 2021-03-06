/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package secinfocrypto.signature;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public final class SignatureChecker extends SignatureAlgorithm<Boolean> {

    private PublicKey pubKey;

    public void execute(SignatureFile file, PublicKey pubKey) {
        this.pubKey = pubKey;
        super.execute(file);
    }

    @Override
    protected void onPreExecute() {
        if (super.getSignatureListener() == null) {
            return;
        }

        StringBuilder str = new StringBuilder();
        str.append("Check file :");
        str.append(super.file.getFile().getName());
        str.append(" [PROCESSING]");

        super.getSignatureListener().setProcessInformation(str.toString());
    }

    @Override
    protected Boolean doInBackground() {

        Boolean verif = null;

        try {
            // Obtention d'une instance de l'objet calculant la signature
            Signature signer = Signature.getInstance(super.file.getAlgorithm());

            // initialisation de l'objet signant avec la clé publique du signataire
            // typiquement cette clé serait extraite d'un certificat
            signer.initVerify(this.pubKey);

            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(super.file.getFile().getAbsolutePath()));

            // Le buffer de lecture
            byte[] buffer = new byte[1024];
            int nr, nrtot = 0;

            // Boucle de lecture
            while ((nr = bin.read(buffer)) != -1) // mise à jour de l'objet signant avec le nouveau bloc de bytes lu
            {
                signer.update(buffer, 0, nr);

                nrtot += nr;
                super.publishProgress(nrtot * 100 / (nrtot + bin.available()));
            }

            // Vérification signature
            // signature est le tableau d'octets contenant la signature
            // il a pu être envoyé comme piéce jointe attachée au document
            // ou il a pu faire l'objet d'un envoi séparé
            verif = signer.verify(super.file.getSignature());

        } catch (Exception ex) {
            Logger.getLogger(SignatureChecker.class.getName()).log(Level.SEVERE, null, ex);
            if (super.getSignatureListener() != null) {
                super.getSignatureListener().exception(ex);
            }
        } finally {
            // verif = true  : la signature recalculée est identique à signature
            // le document peut être accepté
            // verif = false : la signature recalculé est distincte de signature
            // le document doit être rejeté
            return verif;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (super.getSignatureListener() == null) {
            return;
        }

        if (result != null) {
            file.setTestResult(result);
            file.setLastCheckedDate(new Date());

            StringBuilder str = new StringBuilder();
            str.append("Check file :");
            str.append(super.file.getFile().getName());
            str.append(" [DONE]");

            super.getSignatureListener().setProcessInformation(str.toString());

            super.getSignatureListener().setResult(result);
        }
    }
}
