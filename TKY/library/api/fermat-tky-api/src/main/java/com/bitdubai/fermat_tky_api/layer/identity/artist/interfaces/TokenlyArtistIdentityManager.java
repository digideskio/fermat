package com.bitdubai.fermat_tky_api.layer.identity.artist.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.FermatManager;
import com.bitdubai.fermat_tky_api.all_definitions.enums.ArtistAcceptConnectionsType;
import com.bitdubai.fermat_tky_api.all_definitions.enums.ExposureLevel;
import com.bitdubai.fermat_tky_api.all_definitions.enums.ExternalPlatform;
import com.bitdubai.fermat_tky_api.all_definitions.exceptions.IdentityNotFoundException;
import com.bitdubai.fermat_tky_api.layer.identity.artist.exceptions.ArtistIdentityAlreadyExistsException;
import com.bitdubai.fermat_tky_api.layer.identity.artist.exceptions.CantCreateArtistIdentityException;
import com.bitdubai.fermat_tky_api.layer.identity.artist.exceptions.CantGetArtistIdentityException;
import com.bitdubai.fermat_tky_api.layer.identity.artist.exceptions.CantListArtistIdentitiesException;
import com.bitdubai.fermat_tky_api.layer.identity.artist.exceptions.CantUpdateArtistIdentityException;

import java.util.List;
import java.util.UUID;

/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 09/03/16.
 */
public interface TokenlyArtistIdentityManager extends FermatManager{

    /**
     * Through the method <code>listIdentitiesFromCurrentDeviceUser</code> we can get all the artist
     * identities linked to the current logged device user.
     * @return
     * @throws CantListArtistIdentitiesException
     */
    List<Artist> listIdentitiesFromCurrentDeviceUser() throws CantListArtistIdentitiesException;

    /**
     *
     * @param userName
     * @param profileImage
     * @param password
     * @param externalPlatform
     * @param exposureLevel
     * @param artistAcceptConnectionsType
     * @return
     * @throws CantCreateArtistIdentityException
     * @throws ArtistIdentityAlreadyExistsException
     */
    Artist createArtistIdentity(
            String userName, byte[] profileImage, String password,ExternalPlatform externalPlatform,
            ExposureLevel exposureLevel, ArtistAcceptConnectionsType artistAcceptConnectionsType) throws
            CantCreateArtistIdentityException,
            ArtistIdentityAlreadyExistsException;

    /**
     *
     * @param username
     * @param password
     * @param id
     * @param publicKey
     * @param profileImage
     * @param externalPlatform
     * @param exposureLevel
     * @param artistAcceptConnectionsType
     * @throws CantUpdateArtistIdentityException
     */
    Artist updateArtistIdentity(
            String username, String password, UUID id, String publicKey, byte[] profileImage, ExternalPlatform externalPlatform,
            ExposureLevel exposureLevel, ArtistAcceptConnectionsType artistAcceptConnectionsType) throws
            CantUpdateArtistIdentityException;

    /**
     * This method returns a Artist identity
     * @param publicKey
     * @return
     * @throws CantGetArtistIdentityException
     * @throws IdentityNotFoundException
     */
    Artist getArtistIdentity(UUID publicKey) throws
            CantGetArtistIdentityException,
            IdentityNotFoundException;

}
