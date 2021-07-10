package io.bridge.linker.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils {

    /**
     * 使用gzip进行压缩
     */
    public static String gzip(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
      return  Base64.encodeBase64String(out.toByteArray());
    }

    /**
     * <p>Description:使用gzip进行解压缩</p>
     *
     * @param compressedStr
     * @return
     */
    public static String gunzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = Base64.decodeBase64(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        return decompressed;
    }

    public static void main(String[] args){
        String aa = "H4sIAAAAAAAAAHVUXU/cRhT9Kys/teqqmrHZXZunEKjUVCpCJVIeqjwM9rCesPY4YxuyinjIN0oIoKiBhJDmoxBCCxSqbdkuofkzO/byL3LHNmRDUsmyPOce3Tv33HN9XaNObJOIcf88safqgse+E2qDP1/XqO+MkIhqg9oPcaOEa+WSjvBAyRxECJ7S0I9aWQttl/MGUOR/S/LgV3mwkb59IHfeHD99DVGPXOECgunh02SvlSw8gndOgeBp3UkuPBIBrYZNwB1aF5RmRwuOLPye1V0aRiMneEVHgBObONRjtiJauJ849DGSU8OIiKhoZZwGJZx1gvo7mb1c1ma4mPruWkAFo75Ncw0yzAsavElVI932jnx2JNc35dwfcn8/Wb4n55fTl7+kW/+o0PtbWpHmVLpRPl3UO6Ocoo33Xex/iOpiNhURm2SgVnErBVxsBplIuq7ljPF4Iiow4wQbJZ4C5NzztLPZfbfa233VuzGfbu5leckMEdms4RvaDrlPGhdgHDnico8OOY6gYV4UVMwuaqKKys6iJhwsS82I5LQRGhGmzJC0fgd15HxHPlxJ/l3u3d+Xz3bki4W08zpZWUm2f5P7d4YgbCQvH2ALVeXuujZb/liiZpl9JZD5hRLyzc1ue00uvE8WniTtP48f3yuq7C1CYiwXD5KNdwYayFJDN9QLLoHiY1R4LMrtfS1gIjNgMYERapdwVY1Ax5/OioVhTPtZ+S5Uzg6KelTUwTzNYe5HxM7r+MUMllpyfeu48wjyCdrICocuCyCEq1gZOHC5r5jYRGa1gi0TzJv5kg67BFxtw2qqNREusgwrS1OPG0RcIvWswItOsrYN69X7+zauVdA38Nk9WksPt5LHczms0G57FaCzqPxrpXv0MGlt9FqtAb2CynJpDswCIbVATUoEWPo89+NQVbqzLRfbvc3bcndVqeNQP4JZjcbeRLYlho6quoEtCyMTGQhWTXXHQ6Z6LnyrGo5DKkY/VwcUdLI8SvqJOGQ+DH7sRJxazawO1LCR+V5lGCsSK9/d3+i9epseLqZLd0tfWcgwLevrvtpjgk8z2O1TF58Ehvvd7BEBvwAHzpOkEVL1q/Jy083EESP+VXZuxhXu1JVvbe5lYwgDakdsmv5E6/lFsG5WtNkPpe6aGlwFAAA=";

        System.out.println(gunzip(aa));
    }
}