/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package secinfocrypto.signature;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public final class SignatureGenerator extends SignatureAlgorithm<byte[]> {

    PrivateKey priKey;

    public void execute(SignatureFile file, PrivateKey priKey) {
        this.priKey = priKey;

        super.execute(file);
    }

    @Override
    protected void onPreExecute() {
        if (super.getSignatureListener() == null) {
            return;
        }

        StringBuilder str = new StringBuilder();
        str.append("Sign file :");
        str.append(super.file.getFile().getName());
        str.append(" [PROCESSING]");

        super.getSignatureListener().setProcessInformation(str.toString());
    }

    @Override
    protected byte[] doInBackground() {

        byte[] signature = null;

        try {
            // Obtention d'une instance de l'objet calculant la signature
            Signature signer = Signature.getInstance(super.file.getAlgorithm());

            // initialisation de l'objet signant avec la clé privée priKey du signataire
            signer.initSign(this.priKey);

            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(super.file.getFile().getAbsolutePath()));

            // Le buffer de lecture
            byte[] buffer = new byte[1024];
            int nr, nrtot = 0;

            // Boucle de lecture
            while ((nr = bin.read(buffer)) != -1) {
                // mise à jour de l'objet signant avec le nouveau bloc de bytes lu
                signer.update(buffer, 0, nr);

                nrtot += nr;
                super.publishProgress(nrtot * 100 / (nrtot + bin.available()));
            }

            // récupération de la signature
            signature = signer.sign();

            //return signature;
        } catch (Exception ex) {
            Logger.getLogger(SignatureGenerator.class.getName()).log(Level.SEVERE, null, ex);
            if (super.getSignatureListener() != null) {
                super.getSignatureListener().exception(ex);
            }
        } finally {
            return signature;
        }
    }

    @Override
    protected void onPostExecute(byte[] result) {
        if (super.getSignatureListener() == null) {
            return;
        }

        if (result != null) {
            super.file.setSignature(result);
            super.file.setLastSignedDate(new Date());

            StringBuilder str = new StringBuilder();
            str.append("Sign file :");
            str.append(super.file.getFile().getName());
            str.append(" [DONE]");

            super.getSignatureListener().setProcessInformation(str.toString());

            super.getSignatureListener().setResult(result);
        }
    }
}
