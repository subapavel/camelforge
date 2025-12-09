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
          description: "Where AI meets creativity",
        }) }>I will be</Button>
    </div>
  )

}