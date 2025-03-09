from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
import google.generativeai as genai
from PIL import Image
import io

app = FastAPI()

# Enable CORS (for frontend or external API calls)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allow all origins (replace with your frontend URL in production)
    allow_methods=["POST"],  # Allow only POST requests
    allow_headers=["*"],
)

# Initialize the Gemini API client
genai.configure(api_key="AIzaSyCB8yUcnkBif24aiRljElZwLaXRboTWgso")  # Replace with your actual API key

# Analyze image endpoint
@app.post("/analyze-image/")
async def analyze_image(file: UploadFile = File(...)):
    try:
        print("Received request to analyze image")

        # Read and convert the image
        image_data = await file.read()
        print("Image data read successfully")

        image = Image.open(io.BytesIO(image_data))
        print("Image opened successfully")

        # Convert to bytes for Gemini API
        img_byte_arr = io.BytesIO()
        image.save(img_byte_arr, format="PNG")  # Ensure it's a standard format
        img_byte_arr = img_byte_arr.getvalue()
        print("Image converted to bytes successfully")

        # Call the Gemini API with the image
        model = genai.GenerativeModel("gemini-1.5-flash")  # Updated model name
        print("Gemini model loaded successfully")

        # Format the image data for Gemini API
        image_part = {
            "mime_type": "image/png",
            "data": img_byte_arr
        }
        print("Image data formatted for Gemini API")

        prompt = "Analyze the condition of this product image and determine if it is 'as new', 'slight damage', or 'severely damaged'."
        response = model.generate_content([prompt, image_part])
        print("Gemini API response received")

        # Extract the analysis result
        analysis_result = response.text.lower()
        print("Analysis result:", analysis_result)

        # Determine product condition
        if "as new" in analysis_result:
            condition = "as new"
        elif "slight damage" in analysis_result:
            condition = "slight damage"
        elif "severely damaged" in analysis_result:
            condition = "severely damaged"
        else:
            condition = "unknown"

        print("Condition determined:", condition)
        return JSONResponse(content={"condition": condition})

    except Exception as e:
        print("Error occurred:", str(e))
        raise HTTPException(status_code=500, detail=str(e))
