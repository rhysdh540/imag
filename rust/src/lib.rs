mod bindings;

use jni::JNIEnv;
use jni::objects::JPrimitiveArray;
use jni::sys::{jbyteArray, jclass};
use jni_fn::jni_fn;
use oxipng::{Deflaters, indexset, Interlacing, RowFilter, StripChunks};

#[jni_fn("dev.rdh.imag.process.Oxipng")]
pub fn process0(env: JNIEnv, _class: jclass, bytes: jbyteArray) -> jbyteArray {
    let bytes = unsafe { JPrimitiveArray::from_raw(bytes) };
    let bytes = env.convert_byte_array(bytes).unwrap();

    let options = oxipng::Options {
        fix_errors: true,
        force: false,
        filter: indexset! {RowFilter::None, RowFilter::Sub, RowFilter::Entropy, RowFilter::Bigrams},
        interlace: Some(Interlacing::None),
        optimize_alpha: true,
        bit_depth_reduction: true,
        color_type_reduction: true,
        palette_reduction: true,
        grayscale_reduction: true,
        idat_recoding: true,
        scale_16: false,
        strip: StripChunks::All,
        deflate: Deflaters::Libdeflater { compression: 1 }, // use level 1 for speed, zopfli will re-deflate it later
        fast_evaluation: false,
        timeout: None,
    };
    let result = oxipng::optimize_from_memory(&bytes, &options).unwrap();
    env.byte_array_from_slice(&result).unwrap().into_raw()
}
