// Update the import path to the correct location of Button
"use client"
import { toast } from "sonner"

import { Button } from "@shared/ui";

export default function Home() {
  return (
    <div className="flex items-center justify-center h-screen">
      <Button 
        variant="outline" 
        onClick={() =>
        toast("CMS site for Camel Forge", {
          description: "Build using Next.js and Payload cms",
        }) }>I will be</Button>
    </div>
  )

}