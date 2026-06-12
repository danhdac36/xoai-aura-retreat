import fitz
import os

pdf_path = "SRS_Document_SWP391_G6.docx.pdf"
output_dir = "images"

if not os.path.exists(output_dir):
    os.makedirs(output_dir)

doc = fitz.open(pdf_path)

img_count = 0
for i in range(len(doc)):
    for img in doc.get_page_images(i):
        xref = img[0]
        pix = fitz.Pixmap(doc, xref)
        if pix.n - pix.alpha < 4:
            pix.save(f"{output_dir}/page_{i+1}_img_{xref}.png")
        else:
            pix1 = fitz.Pixmap(fitz.csRGB, pix)
            pix1.save(f"{output_dir}/page_{i+1}_img_{xref}.png")
            pix1 = None
        pix = None
        img_count += 1

print(f"Extraction complete. {img_count} images extracted.")
