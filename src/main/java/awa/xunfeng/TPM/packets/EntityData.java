package awa.xunfeng.TPM.packets;

    public enum EntityData {
        ON_FIRE((byte)1),
        CROUCHING((byte)2),
        PREVIOUSLY_RIDING((byte)4),
        SPRINTING((byte)8),
        SWIMMING((byte)16),
        INVISIBLE((byte)32),
        GLOWING((byte)64),
        GLIDING((byte)-128);

        final byte bitMask;

        EntityData(byte bitMask) {
            this.bitMask = bitMask;
        }

        public byte getBitMask() {
            return this.bitMask;
        }

        public boolean isPresent(byte bits) {
            return ((this.bitMask & bits) == this.bitMask);
        }

        public byte setBit(byte bits) {
            return (byte)(bits | this.bitMask);
        }

        public byte unsetBit(byte bits) {
            return (byte)(bits & (this.bitMask ^ 0xFFFFFFFF));
        }
    }
