package io.github.erkko68.filament.gltfio;

import io.github.erkko68.filament.Material;
import io.github.erkko68.filament.MaterialInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MaterialProvider {
    public static class MaterialKey {
        public boolean doubleSided;
        public boolean unlit;
        public boolean hasVertexColors;
        public boolean hasBaseColorTexture;
        public boolean hasNormalTexture;
        public boolean hasOcclusionTexture;
        public boolean hasEmissiveTexture;
        public boolean useSpecularGlossiness;
        public int alphaMode;
        public boolean enableDiagnostics;
        public boolean hasMetallicRoughnessTexture;
        public int metallicRoughnessUV;
        public int baseColorUV;
        public boolean hasClearCoatTexture;
        public int clearCoatUV;
        public boolean hasClearCoatRoughnessTexture;
        public int clearCoatRoughnessUV;
        public boolean hasClearCoatNormalTexture;
        public int clearCoatNormalUV;
        public boolean hasClearCoat;
        public boolean hasTransmission;
        public boolean hasTextureTransforms;
        public int emissiveUV;
        public int aoUV;
        public int normalUV;
        public boolean hasTransmissionTexture;
        public int transmissionUV;
        public boolean hasSheenColorTexture;
        public int sheenColorUV;
        public boolean hasSheenRoughnessTexture;
        public int sheenRoughnessUV;
        public boolean hasVolumeThicknessTexture;
        public int volumeThicknessUV;
        public boolean hasSheen;
        public boolean hasIOR;
        public boolean hasVolume;
        public boolean hasDispersion;
        public boolean hasSpecular;
        public boolean hasSpecularTexture;
        public boolean hasSpecularColorTexture;
        public int specularTextureUV;
        public int specularColorTextureUV;


        public MaterialKey() {}
        static {
            Gltfio.init();
        }

        public void constrainMaterial(@NotNull int[] uvmap) {
            nConstrainMaterial(this, uvmap);
        }

        private static native void nConstrainMaterial(MaterialKey materialKey, int[] uvmap);
    };

    @Nullable
    public MaterialInstance createMaterialInstance(MaterialKey config, @NotNull int[] uvmap, @Nullable String label, @Nullable String extras);

    @Nullable
    public Material getMaterial(MaterialKey config, @NotNull int[] uvmap, @Nullable String label);

    @NotNull
    public Material[] getMaterials();

    public boolean needsDummyData(int attrib);

    public void destroyMaterials();

    public void destroy();
}

