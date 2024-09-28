package im.bclpbkiauv.messenger;

import im.bclpbkiauv.tgnet.TLObject;
import im.bclpbkiauv.tgnet.TLRPC;

public class ImageLocation {
    public long access_hash;
    public int currentSize;
    public int dc_id;
    public TLRPC.Document document;
    public long documentId;
    public byte[] file_reference;
    public byte[] iv;
    public byte[] key;
    public TLRPC.TL_fileLocationToBeDeprecated location;
    public boolean lottieAnimation;
    public String path;
    public TLRPC.Photo photo;
    public long photoId;
    public TLRPC.InputPeer photoPeer;
    public boolean photoPeerBig;
    public TLRPC.PhotoSize photoSize;
    public SecureDocument secureDocument;
    public TLRPC.InputStickerSet stickerSet;
    public String thumbSize;
    public WebFile webFile;

    public static ImageLocation getForPath(String path2) {
        if (path2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.path = path2;
        return imageLocation;
    }

    public static ImageLocation getForSecureDocument(SecureDocument secureDocument2) {
        if (secureDocument2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.secureDocument = secureDocument2;
        return imageLocation;
    }

    public static ImageLocation getForDocument(TLRPC.Document document2) {
        if (document2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.document = document2;
        imageLocation.key = document2.key;
        imageLocation.iv = document2.iv;
        imageLocation.currentSize = document2.size;
        return imageLocation;
    }

    public static ImageLocation getForWebFile(WebFile webFile2) {
        if (webFile2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.webFile = webFile2;
        imageLocation.currentSize = webFile2.size;
        return imageLocation;
    }

    public static ImageLocation getForObject(TLRPC.PhotoSize photoSize2, TLObject object) {
        if (object instanceof TLRPC.Photo) {
            return getForPhoto(photoSize2, (TLRPC.Photo) object);
        }
        if (object instanceof TLRPC.Document) {
            return getForDocument(photoSize2, (TLRPC.Document) object);
        }
        return null;
    }

    public static ImageLocation getForPhoto(TLRPC.PhotoSize photoSize2, TLRPC.Photo photo2) {
        int dc_id2;
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || photo2 == null) {
            return null;
        } else {
            if (photo2.dc_id != 0) {
                dc_id2 = photo2.dc_id;
            } else {
                dc_id2 = photoSize2.location.dc_id;
            }
            return getForPhoto(photoSize2.location, photoSize2.size, photo2, (TLRPC.Document) null, (TLRPC.InputPeer) null, false, dc_id2, (TLRPC.InputStickerSet) null, photoSize2.type);
        }
    }

    public static ImageLocation getForUser(TLRPC.User user, boolean big) {
        int dc_id2;
        if (user == null || user.access_hash == 0 || user.photo == null) {
            return null;
        }
        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
        TLRPC.FileLocation fileLocation = big ? userProfilePhoto.photo_big : userProfilePhoto.photo_small;
        if (fileLocation == null) {
            return null;
        }
        TLRPC.TL_inputPeerUser inputPeer = new TLRPC.TL_inputPeerUser();
        inputPeer.user_id = user.id;
        inputPeer.access_hash = user.access_hash;
        if (user.photo.dc_id != 0) {
            dc_id2 = user.photo.dc_id;
        } else {
            dc_id2 = fileLocation.dc_id;
        }
        return getForPhoto(fileLocation, 0, (TLRPC.Photo) null, (TLRPC.Document) null, inputPeer, big, dc_id2, (TLRPC.InputStickerSet) null, (String) null);
    }

    public static ImageLocation getForChat(TLRPC.Chat chat, boolean big) {
        TLRPC.InputPeer inputPeer;
        int dc_id2;
        if (chat == null || chat.photo == null) {
            return null;
        }
        TLRPC.ChatPhoto chatPhoto = chat.photo;
        TLRPC.FileLocation fileLocation = big ? chatPhoto.photo_big : chatPhoto.photo_small;
        if (fileLocation == null) {
            return null;
        }
        if (!ChatObject.isChannel(chat)) {
            inputPeer = new TLRPC.TL_inputPeerChat();
            inputPeer.chat_id = chat.id;
        } else if (chat.access_hash == 0) {
            return null;
        } else {
            inputPeer = new TLRPC.TL_inputPeerChannel();
            inputPeer.channel_id = chat.id;
            inputPeer.access_hash = chat.access_hash;
        }
        if (chat.photo.dc_id != 0) {
            dc_id2 = chat.photo.dc_id;
        } else {
            dc_id2 = fileLocation.dc_id;
        }
        return getForPhoto(fileLocation, 0, (TLRPC.Photo) null, (TLRPC.Document) null, inputPeer, big, dc_id2, (TLRPC.InputStickerSet) null, (String) null);
    }

    public static ImageLocation getForSticker(TLRPC.PhotoSize photoSize2, TLRPC.Document sticker) {
        TLRPC.InputStickerSet stickerSet2;
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || sticker == null || (stickerSet2 = MediaDataController.getInputStickerSet(sticker)) == null) {
            return null;
        } else {
            ImageLocation imageLocation2 = getForPhoto(photoSize2.location, photoSize2.size, (TLRPC.Photo) null, (TLRPC.Document) null, (TLRPC.InputPeer) null, false, sticker.dc_id, stickerSet2, photoSize2.type);
            if (MessageObject.isAnimatedStickerDocument(sticker)) {
                imageLocation2.lottieAnimation = true;
            }
            return imageLocation2;
        }
    }

    public static ImageLocation getForDocument(TLRPC.PhotoSize photoSize2, TLRPC.Document document2) {
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            ImageLocation imageLocation = new ImageLocation();
            imageLocation.photoSize = photoSize2;
            return imageLocation;
        } else if (photoSize2 == null || document2 == null) {
            return null;
        } else {
            return getForPhoto(photoSize2.location, photoSize2.size, (TLRPC.Photo) null, document2, (TLRPC.InputPeer) null, false, document2.dc_id, (TLRPC.InputStickerSet) null, photoSize2.type);
        }
    }

    public static ImageLocation getForLocal(TLRPC.FileLocation location2) {
        if (location2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
        imageLocation.location = tL_fileLocationToBeDeprecated;
        tL_fileLocationToBeDeprecated.local_id = location2.local_id;
        imageLocation.location.volume_id = location2.volume_id;
        imageLocation.location.secret = location2.secret;
        imageLocation.location.dc_id = location2.dc_id;
        return imageLocation;
    }

    private static ImageLocation getForPhoto(TLRPC.FileLocation location2, int size, TLRPC.Photo photo2, TLRPC.Document document2, TLRPC.InputPeer photoPeer2, boolean photoPeerBig2, int dc_id2, TLRPC.InputStickerSet stickerSet2, String thumbSize2) {
        if (location2 == null) {
            return null;
        }
        if (photo2 == null && photoPeer2 == null && stickerSet2 == null && document2 == null) {
            return null;
        }
        ImageLocation imageLocation = new ImageLocation();
        imageLocation.dc_id = dc_id2;
        imageLocation.photo = photo2;
        imageLocation.currentSize = size;
        imageLocation.photoPeer = photoPeer2;
        imageLocation.photoPeerBig = photoPeerBig2;
        imageLocation.stickerSet = stickerSet2;
        if (location2 instanceof TLRPC.TL_fileLocationToBeDeprecated) {
            imageLocation.location = (TLRPC.TL_fileLocationToBeDeprecated) location2;
            if (photo2 != null) {
                imageLocation.file_reference = photo2.file_reference;
                imageLocation.access_hash = photo2.access_hash;
                imageLocation.photoId = photo2.id;
                imageLocation.thumbSize = thumbSize2;
            } else if (document2 != null) {
                imageLocation.file_reference = document2.file_reference;
                imageLocation.access_hash = document2.access_hash;
                imageLocation.documentId = document2.id;
                imageLocation.thumbSize = thumbSize2;
            }
        } else {
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
            imageLocation.location = tL_fileLocationToBeDeprecated;
            tL_fileLocationToBeDeprecated.local_id = location2.local_id;
            imageLocation.location.volume_id = location2.volume_id;
            imageLocation.location.secret = location2.secret;
            imageLocation.dc_id = location2.dc_id;
            imageLocation.file_reference = location2.file_reference;
            imageLocation.key = location2.key;
            imageLocation.iv = location2.iv;
            imageLocation.access_hash = location2.secret;
        }
        return imageLocation;
    }

    public static String getStippedKey(Object parentObject, Object fullObject, Object strippedObject) {
        if (parentObject instanceof TLRPC.WebPage) {
            if (fullObject instanceof ImageLocation) {
                ImageLocation imageLocation = (ImageLocation) fullObject;
                if (imageLocation.document != null) {
                    fullObject = imageLocation.document;
                } else if (imageLocation.photoSize != null) {
                    fullObject = imageLocation.photoSize;
                } else if (imageLocation.photo != null) {
                    fullObject = imageLocation.photo;
                }
            }
            if (fullObject == null) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + strippedObject;
            } else if (fullObject instanceof TLRPC.Document) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + ((TLRPC.Document) fullObject).id;
            } else if (fullObject instanceof TLRPC.Photo) {
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + ((TLRPC.Photo) fullObject).id;
            } else if (fullObject instanceof TLRPC.PhotoSize) {
                TLRPC.PhotoSize size = (TLRPC.PhotoSize) fullObject;
                if (size.location != null) {
                    return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + size.location.local_id + "_" + size.location.volume_id;
                }
                return "stripped" + FileRefController.getKeyForParentObject(parentObject);
            } else if (fullObject instanceof TLRPC.FileLocation) {
                TLRPC.FileLocation loc = (TLRPC.FileLocation) fullObject;
                return "stripped" + FileRefController.getKeyForParentObject(parentObject) + "_" + loc.local_id + "_" + loc.volume_id;
            }
        }
        return "stripped" + FileRefController.getKeyForParentObject(parentObject);
    }

    public String getKey(Object parentObject, Object fullObject) {
        if (this.secureDocument != null) {
            return this.secureDocument.secureFile.dc_id + "_" + this.secureDocument.secureFile.id;
        }
        TLRPC.PhotoSize photoSize2 = this.photoSize;
        if (photoSize2 instanceof TLRPC.TL_photoStrippedSize) {
            if (photoSize2.bytes.length > 0) {
                return getStippedKey(parentObject, fullObject, this.photoSize);
            }
            return null;
        } else if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id;
        } else {
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return Utilities.MD5(webFile2.url);
            }
            TLRPC.Document document2 = this.document;
            if (document2 == null) {
                String str = this.path;
                if (str != null) {
                    return Utilities.MD5(str);
                }
                return null;
            } else if (document2.id == 0 || this.document.dc_id == 0) {
                return null;
            } else {
                return this.document.dc_id + "_" + this.document.id;
            }
        }
    }

    public boolean isEncrypted() {
        return this.key != null;
    }

    public int getSize() {
        TLRPC.PhotoSize photoSize2 = this.photoSize;
        if (photoSize2 != null) {
            return photoSize2.size;
        }
        SecureDocument secureDocument2 = this.secureDocument;
        if (secureDocument2 == null) {
            TLRPC.Document document2 = this.document;
            if (document2 != null) {
                return document2.size;
            }
            WebFile webFile2 = this.webFile;
            if (webFile2 != null) {
                return webFile2.size;
            }
        } else if (secureDocument2.secureFile != null) {
            return this.secureDocument.secureFile.size;
        }
        return this.currentSize;
    }
}
