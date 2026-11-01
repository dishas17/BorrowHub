const DEFAULT_DATA = {
    users: [
        {
            id: 1,
            name: "Riya Sharma",
            email: "riya.sharma@campus.edu",
            collegeId: "CS2024-042",
            department: "Computer Science",
            year: "2nd Year",
            role: "STUDENT",
            phone: "+91 98765 43210",
            avatar: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
            karma: 140
        },
        {
            id: 2,
            name: "Aryan Patel",
            email: "aryan.patel@campus.edu",
            collegeId: "IT2024-118",
            department: "Information Tech",
            year: "3rd Year",
            role: "STUDENT",
            phone: "+91 98765 43211",
            avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
            karma: 95
        },
        {
            id: 3,
            name: "Neha Kapoor",
            email: "neha.kapoor@campus.edu",
            collegeId: "EC2023-019",
            department: "Electronics & Comm",
            year: "4th Year",
            role: "STUDENT",
            phone: "+91 98765 43212",
            avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
            karma: 210
        },
        {
            id: 4,
            name: "Vikram Singh",
            email: "vikram.singh@campus.edu",
            collegeId: "ME2024-088",
            department: "Mechanical Engg",
            year: "2nd Year",
            role: "STUDENT",
            phone: "+91 98765 43213",
            avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
            karma: 80
        },
        {
            id: 5,
            name: "Sneha Menon",
            email: "sneha.menon@campus.edu",
            collegeId: "BT2024-007",
            department: "Biotechnology",
            year: "1st Year",
            role: "STUDENT",
            phone: "+91 98765 43214",
            avatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80",
            karma: 160
        },
        {
            id: 99,
            name: "Prof. Arvind Sharma",
            email: "admin@borrowhub.edu",
            collegeId: "FACULTY-001",
            department: "Campus Welfare & Resource Cell",
            year: "Faculty Advisor",
            role: "ADMIN",
            phone: "+91 98765 43299",
            avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&auto=format&fit=crop&q=80",
            karma: 500
        }
    ],

    categories: [
        { id: "Textbooks", name: "Textbooks", sub: "Study essentials", icon: "📚", color: "#F43F5E", bg: "#FFE4E6" },
        { id: "Calculators", name: "Calculators", sub: "Ready to borrow", icon: "🧮", color: "#0D9488", bg: "#CCFBF1" },
        { id: "Lab Equipment", name: "Lab Equipment", sub: "For your experiments", icon: "🧪", color: "#8B5CF6", bg: "#EDE9FE" },
        { id: "Sports Gear", name: "Sports Gear", sub: "Play. Stay active.", icon: "🏸", color: "#F97316", bg: "#FFEDD5" },
        { id: "Electronics", name: "Electronics", sub: "Tech & Gadgets", icon: "🔌", color: "#2563EB", bg: "#E0F2FE" },
        { id: "Others", name: "Others", sub: "And more...", icon: "✨", color: "#64748B", bg: "#F1F5F9" }
    ],

    items: [
        {
            id: 1,
            ownerId: 1,
            title: "DSA Textbook",
            category: "Textbooks",
            description: "Data Structures and Algorithms in Java by Robert Lafore. Clean reference book with neat notes for lab practical exams.",
            rate: "Free to borrow",
            condition: "Like New",
            status: "AVAILABLE",
            image: "assets/images/dsa-book.jpg",
            createdAt: "2026-10-20"
        },
        {
            id: 2,
            ownerId: 2,
            title: "Scientific Calculator",
            category: "Calculators",
            description: "Casio fx-991EX ClassWiz with natural textbook display and matrix calculations. Permitted in semester exams.",
            rate: "Free to borrow",
            condition: "Excellent",
            status: "AVAILABLE",
            image: "assets/images/calculator.svg",
            createdAt: "2026-10-21"
        },
        {
            id: 3,
            ownerId: 3,
            title: "Chemistry Lab Coat & Goggles",
            category: "Lab Equipment",
            description: "Pure white 100% cotton lab coat (Size M, washed and pressed) and ANSI certified anti-splash safety goggles.",
            rate: "Free to borrow",
            condition: "Like New",
            status: "AVAILABLE",
            image: "assets/images/lab-coat.jpg",
            createdAt: "2026-10-22"
        },
        {
            id: 4,
            ownerId: 4,
            title: "Badminton Set",
            category: "Sports Gear",
            description: "Pair of Yonex Carbonex rackets with comfortable grip and 3 nylon shuttlecocks. Comes with waterproof cover.",
            rate: "Free to borrow",
            condition: "Good Condition",
            status: "AVAILABLE",
            image: "assets/images/badminton.jpg",
            createdAt: "2026-10-23"
        },
        {
            id: 5,
            ownerId: 5,
            title: "Camera (Canon DSLR)",
            category: "Electronics",
            description: "Canon EOS 1500D DSLR with 18-55mm lens, 64GB high speed memory card, strap, and carry bag for campus events.",
            rate: "Free to borrow",
            condition: "Excellent",
            status: "BORROWED",
            image: "assets/images/camera.jpg",
            createdAt: "2026-10-18"
        },
        {
            id: 6,
            ownerId: 4,
            title: "Engineering Mini Drafter",
            category: "Lab Equipment",
            description: "Omega mini drafter for first-year and mechanical engineering drawing sheets. Smooth steel clamp with protractor.",
            rate: "Free to borrow",
            condition: "Well Calibrated",
            status: "AVAILABLE",
            image: "assets/images/drafter.jpg",
            createdAt: "2026-10-19"
        },
        {
            id: 7,
            ownerId: 2,
            title: "Arduino IoT Sensor Kit",
            category: "Electronics",
            description: "Arduino Uno R3 with ultrasonic sensor, DHT11, servo motor, breadboard, OLED and jumper cables for club hackathons.",
            rate: "Free to borrow",
            condition: "Complete Set",
            status: "AVAILABLE",
            image: "assets/images/arduino.jpg",
            createdAt: "2026-10-24"
        },
        {
            id: 8,
            ownerId: 1,
            title: "Organic Chemistry Molecular Model Kit",
            category: "Lab Equipment",
            description: "Ball and stick molecular modeling set with 120 atoms and flexible bonds for visualizing stereochemistry.",
            rate: "Free to borrow",
            condition: "Like New",
            status: "AVAILABLE",
            image: "assets/images/molecular-kit.jpg",
            createdAt: "2026-10-24"
        }
    ],

    requests: [
        {
            id: 101,
            itemId: 5,
            borrowerId: 2,
            requestDate: "2026-10-21",
            returnDate: "2026-10-28",
            purpose: "Needed for photography coverage of campus cultural fest and hackathon inauguration.",
            status: "APPROVED"
        },
        {
            id: 102,
            itemId: 1,
            borrowerId: 4,
            requestDate: "2026-10-24",
            returnDate: "2026-10-30",
            purpose: "Studying for Data Structures lab test coming up next Tuesday.",
            status: "PENDING"
        }
    ],

    transactions: [
        {
            id: 501,
            requestId: 101,
            itemId: 5,
            borrowerId: 2,
            borrowedDate: "2026-10-21",
            dueDate: "2026-10-28",
            actualReturnDate: null,
            status: "ACTIVE",
            notes: "Checked camera sensor and accessories during campus handover."
        }
    ]
};
